package org.testng;

import bsh.EvalError;
import bsh.Interpreter;
import org.testng.collections.Maps;
import org.testng.log4testng.Logger;

import java.lang.reflect.Method;
import java.util.Map;

public final class BeanShellScriptedMethodSelector implements IScriptedMethodSelector {

  public static final String TESTNG_METHOD = "testngMethod";
  private static final String BEANSHELL = "beanshell";

  private static final Logger LOGGER = Logger.getLogger(BeanShellScriptedMethodSelector.class);
  public static final String METHOD = "method";
  public static final String GROUPS = "groups";

  @Override
  public boolean includeMethodFromExpression(String expression, ITestNGMethod tm) {
    boolean result = false;

    Interpreter interpreter = getInterpreter();
    try {
      Map<String, String> groups = Maps.newHashMap();
      for (String group : tm.getGroups()) {
        groups.put(group, group);
      }
      setContext(interpreter, tm.getConstructorOrMethod().getMethod(), groups, tm);
      Object evalResult = interpreter.eval(expression);
      if (evalResult == null) {
        String msg = String.format("The beanshell expression [%s] evaluated to null.", expression);
        throw new TestNGException(msg);
      }
      result = (Boolean) evalResult;
    } catch (EvalError evalError) {
      LOGGER.error(
          "Cannot evaluate expression:" + expression + ":" + evalError.getMessage(), evalError);
    } finally {
      resetContext(interpreter);
    }

    return result;
  }

  @Override
  public boolean accept(String language) {
    return BEANSHELL.equalsIgnoreCase(language);
  }

  private static Interpreter getInterpreter() {
    return InterpreterHolder.getInstance();
  }

  private static void setContext(
      Interpreter interpreter, Method method, Map<String, String> groups, ITestNGMethod tm) {
    try {
      interpreter.set(METHOD, method);
      interpreter.set(GROUPS, groups);
      interpreter.set(TESTNG_METHOD, tm);
    } catch (EvalError evalError) {
      throw new TestNGException("Cannot set BSH interpreter", evalError);
    }
  }

  private static void resetContext(Interpreter interpreter) {
    try {
      interpreter.unset(METHOD);
      interpreter.unset(GROUPS);
      interpreter.unset(TESTNG_METHOD);
    } catch (EvalError evalError) {
      LOGGER.error("Cannot reset interpreter:" + evalError.getMessage(), evalError);
    }
  }

  private static class InterpreterHolder {
    private static Interpreter interpreter = new Interpreter();

    public static Interpreter getInstance() {
      return interpreter;
    }
  }
}
