/*
 * Copyright 2015 Codice Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package TextFile;

import org.codice.testify.objects.*;
import org.codice.testify.writers.Writer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The TextFileWriter class is a Testify Writer service to create text files from the test results
 */
public class TextFileWriter implements BundleActivator, Writer {

    @Override
    public void writeResults(TestData testData, Response response, Result result) {

        TestifyLogger.debug("Running TextFileWriter", this.getClass().getSimpleName());

        //Set test status for file header and file name
        String testStatus = "Success";
        String resultString = "";
        if (!result.getTestResult()) {
            testStatus = "Failed (See assertion results below)";
            resultString = "-FAILED";
        }

        //Format assertion results string for text file
        String assertionResults = "";
        for (String assertion : result.getAssertionResults().keySet()) {
            assertionResults = assertionResults + System.lineSeparator() + "Assertion: " + assertion + " Result: " + result.getAssertionResults().get(assertion);
        }

        //Set text of the result body
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + testStatus + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + assertionResults + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse();

        //Add Response Code to text if it exists
        if (response.getResponseCode() != -1) {
            resultBody = resultBody + System.lineSeparator() + "Response Code: " + response.getResponseCode();
        }

        //Add Response Headers to text if they exist
        if (response.getResponseHeaders() != null) {
            resultBody = resultBody + System.lineSeparator() + "Response Headers: " + response.getResponseHeaders();
        }

        //Add Response Attachments to text if they exist
        if (response.getResponseAttachments() != null) {
            resultBody = resultBody + System.lineSeparator() + "Response Attachments: " + response.getResponseAttachments();
        }

        BufferedWriter writer = null;
        try {

            //Create a result file
            String testName = testData.getTestName().substring(0, testData.getTestName().lastIndexOf("."));
            File resultFile = new File( testData.getResultFolder() + "_" + AllObjects.getObject("timestamp"));
            resultFile.mkdirs();
            resultFile = new File( resultFile,  testName + resultString + ".txt" );

            //Write the results to the result file
            writer = new BufferedWriter(new FileWriter(resultFile));
            writer.write(resultBody);


        } catch (IOException e) {
            TestifyLogger.error(e.getMessage(), this.getClass().getSimpleName());
        } finally {
            try {

                //Close the writer regardless of earlier results
                if (writer != null) {
                    writer.close();
                }

            } catch (IOException e) {
                TestifyLogger.error(e.getMessage(), this.getClass().getSimpleName());
            }
        }
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {

        //Register the Contains service
        bundleContext.registerService(Writer.class.getName(), new TextFileWriter(), null);

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}