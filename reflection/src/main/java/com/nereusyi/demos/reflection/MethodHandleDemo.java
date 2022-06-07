package com.nereusyi.demos.reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public class MethodHandleDemo {

    public static void main(String[] args) throws Throwable {
        // get class
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class<?> personClass = lookup.findClass(Person.class.getName());
        Person p = (Person) personClass.getDeclaredConstructor().newInstance();

        // find filed , read and write field
        /*
        MethodHandle fieldReader = lookup.findGetter(personClass, "name", String.class);// private field , throw exception
        Object readResult = fieldReader.invoke(p);
        System.out.println(readResult);*/

        // public field
        System.out.println("p.job=" + p.job);
        MethodHandle jobSetter = lookup.findSetter(personClass, "job", String.class);
        jobSetter.invoke(p, "fun job");
        System.out.println("set job...");
        System.out.println("p.job=" + p.job);


        MethodHandle jobReader = lookup.findGetter(personClass, "job", String.class);// non-private
        Object readResult = jobReader.invoke(p);
        System.out.println("jobReader="+readResult);

        // private field - Java 8
        System.out.println("p.getName=" + p.getName());
        Field nameField8 = Person.class.getDeclaredField("name");
        nameField8.setAccessible(true);


        MethodHandle privateNameSetter = lookup.unreflectSetter(nameField8);
        privateNameSetter.invoke(p, "MyName");
        System.out.println("p.getName=" + p.getName());

        MethodHandle privateNameGetter = lookup.unreflectGetter(nameField8);
        Object readName = privateNameGetter.invoke(p);
        System.out.println("readPrivateName="+readName);


        // private field - since Java 9
        System.out.println("p.getLocation=" + p.getLocation());
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(Person.class, lookup);


        MethodHandle locationSetter = privateLookup.findSetter(Person.class, "location", String.class);
        locationSetter.invoke(p, "MyLocation");

        MethodHandle locationGetter = privateLookup.findGetter(Person.class, "location", String.class);
        Object locationGetterResult = locationGetter.invoke(p);
        System.out.println("locationGetterResult=" + locationGetterResult);


        // find method and invoke
        MethodType setterMethod = MethodType.methodType(void.class, String.class);
        MethodHandle setterHandle = lookup.findVirtual(personClass, "setName", setterMethod);
        setterHandle.invoke(p, "HelloWorld");

        MethodType getterMethod = MethodType.methodType(String.class);
        MethodHandle getterHandle = lookup.findVirtual(personClass, "getName", getterMethod);
        Object invoke = getterHandle.invoke(p);
        System.out.println(invoke);

        MethodType printMethodType = MethodType.methodType(void.class);
        MethodHandle print = lookup.findVirtual(Person.class, "print", printMethodType);
        print.invoke(p);
    }
}
