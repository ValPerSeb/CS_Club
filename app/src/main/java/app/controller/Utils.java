
package app.controller;

import app.controller.validator.CommonsValidator;
import java.util.Scanner;

public abstract class Utils {
    private static Scanner reader = new Scanner(System.in);
    private static CommonsValidator validator = new CommonsValidator();

    public static Scanner getReader(){
        return reader;
    }
    
    public static CommonsValidator getValidator(){
        return validator;
    }
}
