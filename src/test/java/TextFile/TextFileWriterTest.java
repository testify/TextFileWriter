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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Scanner;

@RunWith(JUnit4.class)
public class TextFileWriterTest {

    //Set objects
    private final TextFileWriter testFileWriter = new TextFileWriter();
    private final String testFileName = "TestFile";
    private final String timeStamp = "TIME STAMP";
    private final Request request = new Request("type", "endpoint", "testBlock");
    private final ParsedData parsedData = new ParsedData(request, null, null);
    private final LinkedHashMap<String,String> assertionResults = new LinkedHashMap<>();
    private final String assertion = "Assertion";
    private final String assertionResult = "Result";
    private final String assertionResultsString = "Assertion: " + assertion + " Result: " + assertionResult;
    private final Response response = new Response(null);

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testFolderNameGeneration() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        testFileWriter.writeResults(testData, response, result);
        File specificResultFolder = new File(resultFolder + "_" + timeStamp);
        assert( specificResultFolder.isDirectory() );
    }

    @Test
    public void testSuccessfulTestFileNameGeneration() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        testFileWriter.writeResults(testData, response, result);
        File specificResultFolder = new File(resultFolder + "_" + timeStamp);
        File resultFile = new File(specificResultFolder + File.separator + testFileName + ".txt");
        assert( resultFile.exists() );
    }

    @Test
    public void testFailedTestFileNameGeneration() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(false, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        testFileWriter.writeResults(testData, response, result);
        File specificResultFolder = new File(resultFolder + "_" + timeStamp);
        File resultFile = new File(specificResultFolder + File.separator + testFileName + "-FAILED.txt");
        assert( resultFile.exists() );
    }

    @Test
    public void testFileContentSuccess() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        testFileWriter.writeResults(testData, response, result);
        File fileName = new File(resultFolder + "_" + timeStamp + File.separator + testFileName + ".txt");
        String testString = null;
        try {
            Scanner scanner = new Scanner(fileName);
            testString = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + "Success" + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + System.lineSeparator() + assertionResultsString + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse();
        assert( testString != null );
        assert( testString.equals(resultBody) );
    }

    @Test
    public void testFileContentFailed() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(false, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        testFileWriter.writeResults(testData, response, result);
        File fileName = new File(resultFolder + "_" + timeStamp + File.separator + testFileName + "-FAILED.txt");
        String testString = null;
        try {
            Scanner scanner = new Scanner(fileName);
            testString = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + "Failed (See assertion results below)" + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + System.lineSeparator() + assertionResultsString + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse();
        assert( testString != null );
        assert( testString.equals(resultBody) );
    }

    @Test
    public void testFileContentWithCode() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        response.setResponseCode(1);
        testFileWriter.writeResults(testData, response, result);
        File fileName = new File(resultFolder + "_" + timeStamp + File.separator + testFileName + ".txt");
        String testString = null;
        try {
            Scanner scanner = new Scanner(fileName);
            testString = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + "Success" + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + System.lineSeparator() + assertionResultsString + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse() +
                System.lineSeparator() + "Response Code: " + response.getResponseCode();
        assert( testString != null );
        assert( testString.equals(resultBody) );
    }

    @Test
    public void testFileContentWithHeaders() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        response.setResponseHeaders("HEADERS");
        testFileWriter.writeResults(testData, response, result);
        File fileName = new File(resultFolder + "_" + timeStamp + File.separator + testFileName + ".txt");
        String testString = null;
        try {
            Scanner scanner = new Scanner(fileName);
            testString = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + "Success" + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + System.lineSeparator() + assertionResultsString + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse() +
                System.lineSeparator() + "Response Headers: " + response.getResponseHeaders();
        assert( testString != null );
        assert( testString.equals(resultBody) );
    }

    @Test
    public void testFileContentWithAttachments() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        response.setResponseAttachments("ATTACHMENTS");
        testFileWriter.writeResults(testData, response, result);
        File fileName = new File(resultFolder + "_" + timeStamp + File.separator + testFileName + ".txt");
        String testString = null;
        try {
            Scanner scanner = new Scanner(fileName);
            testString = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + "Success" + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + System.lineSeparator() + assertionResultsString + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse() +
                System.lineSeparator() + "Response Attachments: " + response.getResponseAttachments();
        assert( testString != null );
        assert( testString.equals(resultBody) );
    }

    @Test
    public void testFileContentWithEverything() {
        AllObjects.setObject("timestamp","TIME STAMP");
        File tempResultFolder = null;
        try {
            tempResultFolder = tempFolder.newFolder("results");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resultFolder = tempResultFolder.toString();
        assertionResults.put(assertion, assertionResult);
        Result result = new Result(true, assertionResults);
        TestData testData = new TestData(testFileName + ".xml", parsedData, resultFolder);
        response.setResponseCode(1);
        response.setResponseHeaders("HEADERS");
        response.setResponseAttachments("ATTACHMENTS");
        testFileWriter.writeResults(testData, response, result);
        File fileName = new File(resultFolder + "_" + timeStamp + File.separator + testFileName + ".txt");
        String testString = null;
        try {
            Scanner scanner = new Scanner(fileName);
            testString = scanner.useDelimiter("\\Z").next();
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String resultBody = "Results for test: " + testData.getTestName() + " --------- " + "Success" + System.lineSeparator() + System.lineSeparator() +
                "Endpoint: " + testData.getParsedData().getRequest().getEndpoint() + System.lineSeparator() + System.lineSeparator() +
                "Test: " + testData.getParsedData().getRequest().getTestBlock() + System.lineSeparator() + System.lineSeparator() +
                "Assertion Results: " + System.lineSeparator() + assertionResultsString + System.lineSeparator() + System.lineSeparator() +
                "Response: " + response.getResponse() +
                System.lineSeparator() + "Response Code: " + response.getResponseCode() +
                System.lineSeparator() + "Response Headers: " + response.getResponseHeaders() +
                System.lineSeparator() + "Response Attachments: " + response.getResponseAttachments();
        assert( testString != null );
        assert( testString.equals(resultBody) );
    }

}