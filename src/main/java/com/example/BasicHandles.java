package com.example;

import java.io.PrintStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SwitchPoint;

public class BasicHandles {

    public static void main(String[] args) throws Throwable {

        // method invocation
        MethodHandle toUpperCaseMH = LOOKUP.findVirtual(String.class, "toUpperCase", MethodType.methodType(String.class));
        Object uppercaseString = toUpperCaseMH.invoke("Hello, world");
        System.out.println(uppercaseString);

        MethodHandle toLowerCaseMH = LOOKUP.findVirtual(String.class, "toLowerCase", MethodType.methodType(String.class));
        String lowercaseString = (String) toLowerCaseMH.invoke("Hello, world");
        System.out.println(lowercaseString);


        println(System.out, "Welcome To Method Handle World");
        println("Welcome To Wonderful Method Handle World");


        // field get
        MethodHandle systemOutMH = LOOKUP
                .findStaticGetter(System.class, "out", PrintStream.class);

        PrintStream out3 = (PrintStream) systemOutMH.invoke();
        println(out3, "Welcome To indy World");


        // field set
        class Person {
            String name;
        }
        Person person = new Person();
        person.name = "Ahmed";
        println(person.name);
        MethodHandle dateSetterMH = LOOKUP.findSetter(Person.class, "name", String.class);
        dateSetterMH.invoke(person, "Ali");
        println(person.name);


        // filter
        MethodHandle filteredPrintLnMH = MethodHandles.filterArguments(SYS_OUT_PRINT_LN_MH, 0, toUpperCaseMH);
        // print "WELCOME TO METHOD HANDLE WORLD"
        filteredPrintLnMH.invoke("Welcome To Method Handle World");


        // switch branch
        SwitchPoint upperLowerSwitch = new SwitchPoint();
        MethodHandle caseChangeMH = upperLowerSwitch.guardWithTest(toUpperCaseMH, toLowerCaseMH);
        println((String) caseChangeMH.invoke("MyString"));
        SwitchPoint.invalidateAll(new SwitchPoint[]{upperLowerSwitch});
        println((String) caseChangeMH.invoke("MyString"));

    }

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final MethodHandle PRINT_LN_MH;
    private static final MethodHandle SYS_OUT_PRINT_LN_MH;

    static {
        try {
            PRINT_LN_MH = LOOKUP.findVirtual(PrintStream.class, "println", MethodType.methodType(void.class, String.class));
            SYS_OUT_PRINT_LN_MH = MethodHandles.insertArguments(PRINT_LN_MH, 0, System.out );
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void println(PrintStream printStream, String s) {
        try {
            PRINT_LN_MH.invoke(printStream, s);
        } catch (Throwable e) {}
    }

    private static void println(String s) {
        try {
            SYS_OUT_PRINT_LN_MH.invoke(s);
        } catch (Throwable e) {}
    }
}
