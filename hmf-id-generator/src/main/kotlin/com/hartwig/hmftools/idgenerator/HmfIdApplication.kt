package com.hartwig.hmftools.idgenerator

import com.hartwig.hmftools.extensions.cli.createCommandLine
import com.hartwig.hmftools.extensions.cli.options.HmfOptions
import com.hartwig.hmftools.extensions.cli.options.strings.InputOption
import com.hartwig.hmftools.extensions.cli.options.strings.RequiredInputOption
import com.hartwig.hmftools.extensions.cli.options.strings.RequiredOutputOption
import com.hartwig.hmftools.extensions.csv.CsvReader
import com.hartwig.hmftools.extensions.csv.CsvWriter
import com.hartwig.hmftools.idgenerator.anonymizedIds.HmfSampleIdCsv
import com.hartwig.hmftools.patientdb.dao.DatabaseAccess
import org.apache.commons.cli.CommandLine
import org.apache.logging.log4j.LogManager
import kotlin.system.exitProcess

private val logger = LogManager.getLogger("HmfIdApplication")

fun main(args: Array<String>) {
    logger.info("Running id-generator")

    val hmfOptions = HmfOptions()
    DatabaseAccess.addDatabaseCmdLineArgs(hmfOptions.options, true)
    hmfOptions.add(RequiredInputOption(PASSWORD, "password"))
    hmfOptions.add(InputOption(NEW_PASSWORD, "password used to generate hashes in HMF ids file"))
    hmfOptions.add(RequiredInputOption(HASH_FILE_IN, "input hash file location"))
    hmfOptions.add(RequiredOutputOption(HASH_FILE_OUT, "output hash file location"))

    run(hmfOptions.createCommandLine("hmf-id", args))
}

private fun run(cmd: CommandLine) {
    logger.info("Updating IDs based on AMBER database data")
    val password = cmd.getOptionValue(PASSWORD)
    val newPassword = cmd.getOptionValue(NEW_PASSWORD, password)
    val databaseAccess = DatabaseAccess.databaseAccess(cmd)

    val amberPatients = databaseAccess.readAmberPatients()
    logger.info("Retrieved ${amberPatients.size} samples from database")

    val existingMappings = databaseAccess.readAmberAnonymous().toSet()
    logger.info("Retrieved ${existingMappings.size} sample mappings from database")
    if (existingMappings.isEmpty()) {
        logger.error("Current amber anonymous table seems to be truncated. Exiting")
        exitProcess(1)
    }

    val hashFileIn = cmd.getOptionValue(HASH_FILE_IN)
    val currentIds = CsvReader.readCSVByName<HmfSampleIdCsv>(hashFileIn).map { it.toHmfSampleId() }
    logger.info("Retrieved ${currentIds.size} samples hashes from ${hashFileIn}")

    logger.info("Processing samples")
    val amberAnonymizer = PatientAnonymizer(password, newPassword)
    val result = amberAnonymizer.anonymize(amberPatients, currentIds)
    val newMappings = AnonymizedRecord(newPassword, result, amberPatients.map { it.sample() }).map { x -> x.toAmberAnonymous() }

    val existingMappingsThatNoLongerExist = existingMappings.subtract(newMappings)
    if (existingMappingsThatNoLongerExist.isEmpty()) {
        logger.info("Check successful: All ${existingMappings.size} existing mappings from amberAnonymous still exist.")
    } else {
        for (missing in existingMappingsThatNoLongerExist) {
            logger.error("Previous mapping ${missing} is no longer found")
        }
        exitProcess(1)
    }

    // Write to file and database
    val hashFileOut = cmd.getOptionValue(HASH_FILE_OUT)
    logger.info("Writing ${result.size} samples hashes to ${hashFileOut}")
    CsvWriter.writeCSV(result.map { it.toCsv() }, hashFileOut)
    logger.info("Writing ${newMappings.size} sample mappings to database")
    databaseAccess.writeAmberAnonymous(newMappings)
    logger.info("Complete")
}
