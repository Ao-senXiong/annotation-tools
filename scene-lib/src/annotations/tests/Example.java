package annotations.tests;

import checkers.nullness.quals.NonNull;
import checkers.javari.quals.ReadOnly;

import java.io.*;
import java.util.*;
import java.lang.annotation.RetentionPolicy;

import annotations.*;
import annotations.el.*;
import annotations.field.*;
import annotations.io.*;

/**
 * Prints information about ReadOnly and NonNull annotations on a given class.
 * Invoke as:
 * <pre>
 * java Example <i>input.jann</i> <i>ClassToProcess</i> <i>output.jann</i>
 * </pre>
 **/
public class Example {
  public static void main(String /*@ReadOnly*/ [] args) {
    AScene scene;

    // System.out.println("Reading in " + args[0]);
    try {
      scene = new AScene();
      IndexFileParser.parseFile(args[0], scene);
    } catch (IOException e) {
      e.printStackTrace(System.err);
      return;
    }

    System.out.println("Processing class " + args[1]);
    // Get a handle on the class
    AClass clazz1 = scene.classes.get(args[1]);
    if (clazz1 == null) {
      System.out.println("Class " + args[1] + " is not mentioned in annotation file " + args[0]);
      return;
    }
    AClass clazz = (AClass) clazz1;

    for (Map.Entry<String, AMethod> me : clazz.methods.entrySet()) {
      AMethod method = me.getValue();

      Annotation rro = method.receiver.lookup("ReadOnly");
      if (rro == null)
        System.out.println("Method " + me.getKey()
            + " might modify the receiver");
      else
        System.out.println("Method " + me.getKey()
            + " must not modify the receiver");

      ATypeElement param1 = method.parameters.vivify(0);
      Annotation p1nn = param1.lookup("NonNull");
      if (p1nn == null) {
        System.out.println("Annotating first parameter of "
            + me.getKey() + " nonnull");

        param1.tlAnnotationsHere.add(Annotations.aNonNull);
      }
    }

    // System.out.println("Writing out " + args[2]);
    try {
      IndexFileWriter.write(scene, new FileWriter(args[2]));
    } catch (IOException e) {
      e.printStackTrace(System.err);
      return;
    } catch (DefException e) {
      e.printStackTrace(System.err);
      return;
    }

    System.out.println("Success.");
  }
}
