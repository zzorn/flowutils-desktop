package org.flowutils.desktop.classbuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.HashMap;


public class ClassBuilderTest {

    private ClassBuilder<TestCalculation1> builder;

    @Before
    public void setUp() throws Exception {
        builder = new JaninoClassBuilder<TestCalculation1>(
                TestCalculation1.class,
                "calculateSomeStuff",
                "fooString", "colors", "numbers");
    }

    @Test
    public void testClassBuilder() throws Exception {

        builder.addSourceLine(SourceLocation.BEFORE_CALCULATION, "int sum = 0;");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "for (int i = 0; i < numbers.length; i++) {");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "  sum += numbers[i];");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "}");
        builder.addSourceLine(SourceLocation.AFTER_CALCULATION, "return sum;");
        builder.addImport(HashMap.SimpleEntry.class);
        builder.addImport(Boolean.TYPE);

        //System.out.println("Source: \n" + builder.createSource());

        final TestCalculation1 calculator = builder.createInstance();

        Assert.assertEquals(0, calculator.calculateSomeStuff("foo", null));
        Assert.assertEquals(3, calculator.calculateSomeStuff("foo", null, 3));
        Assert.assertEquals(2+4+8, calculator.calculateSomeStuff("bar", null, 2, 4, 8));
    }

    @Test
    public void testUniqueIds() throws Exception {

        final String testVar1 = builder.createUniqueIdentifier();
        final String testVar2 = builder.createUniqueIdentifier("testVariable");
        final String testVar3 = builder.createUniqueIdentifier("testVariable");
        final String testVar4 = builder.createUniqueIdentifier("testVariable", "postfix");

        builder.addSourceLine(SourceLocation.BEFORE_CALCULATION, "int sum = 0;");
        builder.addSourceLine(SourceLocation.BEFORE_CALCULATION, "int " + testVar1 + " = 3;");
        builder.addSourceLine(SourceLocation.BEFORE_CALCULATION, "int " + testVar2 + " = 4;");
        builder.addSourceLine(SourceLocation.BEFORE_CALCULATION, "int " + testVar3 + " = 5;");
        builder.addSourceLine(SourceLocation.BEFORE_CALCULATION, "int " + testVar4 + " = 6;");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "  sum += " + testVar1 + ";");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "  sum += " + testVar2 + ";");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "  sum += " + testVar3 + ";");
        builder.addSourceLine(SourceLocation.AT_CALCULATION, "  sum += " + testVar4 + ";");
        builder.addSourceLine(SourceLocation.AFTER_CALCULATION, "return sum;");

        System.out.println("Source: \n" + builder.createSource());

        final TestCalculation1 calculator = builder.createInstance();

        Assert.assertEquals(3+4+5+6, calculator.calculateSomeStuff("bar", null, 99, 100, 111));

    }

    @Test
    public void testInvalidIdentifierPrefixesAndPostfixes() throws Exception {

        checkIdentifierFails(builder, "", "");
        checkIdentifierFails(builder, " ", null);
        checkIdentifierFails(builder, null, " ");
        checkIdentifierFails(builder, "_", null);
        checkIdentifierFails(builder, null, "_");
        checkIdentifierFails(builder, "_", "_");
        checkIdentifierFails(builder, "asd_asd", "ewr");
        checkIdentifierFails(builder, "asdöäasd", "asdff");

    }

    private void checkIdentifierFails(ClassBuilder<TestCalculation1> builder, final String prefix, final String postifx) {
        try {
            String testId= builder.createUniqueIdentifier(prefix, postifx);
            Assert.fail("Should throw IllegalArgumentException, instead got identifier " + testId);
        }
        catch (IllegalArgumentException e) {
            // Expected
        }
    }


    public static interface TestCalculation1 {
        int calculateSomeStuff(String aString, Color[] colors, int... numbers);
    }
}
