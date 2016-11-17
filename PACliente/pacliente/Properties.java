package pacliente;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bernardovieira
 */
public class Properties
{
    public static String    COMMAND_DISCONNECT          = "disconnect";
    public static String    COMMAND_CUR_DIR_PATH        = "pwd";
    public static String    COMMAND_REGISTER            = "register";
    public static String    COMMAND_LOGIN               = "login";
    public static String    COMMAND_LOGOUT              = "logout";
    
    public static Integer   ERROR_ALREADY_REGISTERED    = 1001;
    public static Integer   ERROR_ACCOUNT_NOT_FOUND     = 1002;
    public static Integer   ERROR_WRONG_PASSWORD        = 1003;
    public static Integer   ERROR_MISSING_PARAMS        = 1004;
    public static Integer   ERROR_ALREADY_LOGGED        = 1005;
    
    public static Integer   SUCCESS_REGISTER            = 2001;
    public static Integer   SUCCESS_LOGGED              = 2002;
}
