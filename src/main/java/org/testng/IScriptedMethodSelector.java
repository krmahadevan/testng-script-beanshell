package org.testng;

/**
 * Represents the capabilities to be possessed by an implementation which is capable of parsing an
 * expression from the suite xml file in TestNG and help TestNG decide if a particular method should
 * be included or excluded from execution.
 */
// TODO: This interface will eventually move into TestNG codebase and will not live here. But for
// the sake
// of ensuring that this project can first compile properly, its being temporarily accomodated here.
// This interface should move into TestNG 7.0.0 before it gets released.
public interface IScriptedMethodSelector {

  /**
   * @param expression - An expression as a string that should be evaluated.
   * @param tm - An {@link ITestNGMethod} object that represents the test method for which a
   *     decision is to be made for.
   * @return - <code>true</code> if the method should be included in the execution cycle and <code>
   *     false</code> otherwise.
   */
  boolean includeMethodFromExpression(String expression, ITestNGMethod tm);

  /**
   * @param language - The language of the expression found in the suite xml.
   * @return - <code>true</code> if the current implementation is capable of handling it.
   */
  boolean accept(String language);
}
