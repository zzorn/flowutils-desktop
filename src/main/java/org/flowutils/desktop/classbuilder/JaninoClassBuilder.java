package org.flowutils.desktop.classbuilder;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.janino.SimpleCompiler;
import org.flowutils.Check;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.flowutils.Check.notNull;

/**
 * Implementation of ClassBuilder using the Janino library for generating the code.
 */
public final class JaninoClassBuilder<T> implements ClassBuilder<T> {

    private static final String GENERATED_CLASS_PACKAGE = "org.flowutils.classbuilder.generated";
    private static final String GENERATED_CLASS_NAME = "GeneratedClass";
    private static final String GENERATED_CLASS_FULL_PATH = GENERATED_CLASS_PACKAGE + "." + GENERATED_CLASS_NAME;

    private static final int MAXIMUM_PREFIX_LENGTH = 128;
    private static final int MAXIMUM_POSTFIX_LENGTH = 128;

    private final Class<T> implementedClass;
    private final Set<Class> importedClasses = new LinkedHashSet<Class>();

    private final Map<SourceLocation, StringBuilder> sourcePortions = new HashMap<SourceLocation, StringBuilder>();

    private final Method calculationMethod;
    private final String[] calculationMethodParameterNames;

    private String name;

    private String generatedSource;
    private Class<? extends T> generatedClass;

    private final AtomicLong nextFreeUniqueId = new AtomicLong(1);

    /**
     * @param implementedClass interface or class that the generated class implements or extends.
     * @param calculationMethodName name of the method that does the main calculation, and that code can be generated into.
     *                              There should not be several methods with this name in the implemented class.
     */
    public JaninoClassBuilder(Class<T> implementedClass,
                              String calculationMethodName,
                              String... calculationMethodParameterNames) {
        notNull(implementedClass, "implementedClass");
        Check.identifier(calculationMethodName, "calculationMethodName");
        for (String calculationMethodParameterName : calculationMethodParameterNames) {
            Check.identifier(calculationMethodParameterName, "one of the calculation method parameter names");
        }

        this.implementedClass = implementedClass;
        this.calculationMethodParameterNames = calculationMethodParameterNames;
        this.name = "Generated " + implementedClass.getSimpleName();

        // Find calculation method to implement
        calculationMethod = getMethod(implementedClass, calculationMethodName);
        if (calculationMethod == null) {
            throw new IllegalArgumentException("No method with the name '"+calculationMethodName+"' " +
                                               "found in the class to be implemented ("+implementedClass.getName()+")");
        }

        if (Modifier.isFinal(calculationMethod.getModifiers())) {
            throw new IllegalArgumentException("The calculation method '" + calculationMethodName + "' " +
                                               "is final in the class to be implemented (" + implementedClass.getName() + ")");
        }

        if (Modifier.isPrivate(calculationMethod.getModifiers())) {
            throw new IllegalArgumentException("The calculation method '" + calculationMethodName + "' " +
                                               "is private in the class to be implemented (" + implementedClass.getName() + ")");
        }

        Check.equal(calculationMethodParameterNames.length,
                    "number of calculation method parameter names provided",
                    calculationMethod.getParameterTypes().length,
                    "Number of calculation method parameters returned by Java reflection");

        // Setup string builders for each location
        for (SourceLocation sourceLocation : SourceLocation.values()) {
            sourcePortions.put(sourceLocation, new StringBuilder());
        }

        // Add imports for the class/interface to extend/implement, and the return and parameter types of the calculation method to implement.
        addImport(implementedClass);
        addImport(calculationMethod.getReturnType());
        for (Class<?> parameterType : calculationMethod.getParameterTypes()) {
            addImport(parameterType);
        }

    }

    /**
     * @return descriptive name of the generated class.  Not actually used as the class name, but used in error messages and similar.
     *         Has a default based on the implemented interface / class.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name descriptive name of the generated class.  Not actually used as the class name, but used in error messages and similar.
     *         Has a default based on the implemented interface / class.
     */
    public void setName(String name) {
        Check.nonEmptyString(name, "name");
        this.name = name;
    }

    @Override public void addSourceLine(SourceLocation location, String line) {
        notNull(location, "location");
        notNull(line, "line");

        sourcePortions.get(location).
                append(location.getIndent()).
                              append(line).
                              append("\n");

        codeChanged();
    }


    @Override public void addImport(Class type) {
        notNull(type, "type");

        if (type.isArray()) {
            // Import the component type of the array
            addImport(type.getComponentType());
        }
        else if (!type.isPrimitive()) {
            // Add import statement, if not already added
            if (!importedClasses.contains(type)) {
                importedClasses.add(type);

                addSourceLine(SourceLocation.IMPORTS, "import " + type.getCanonicalName() + ";");
            }
        }
    }


    @Override public String createSource() {

        if (generatedSource == null) {
            // Support both extending a base class or implementing an interface
            String implementsOrExtends = implementedClass.isInterface() ? "implements" : "extends";

            // Construct code for parameter section of calculation method definition
            StringBuilder calculationMethodParameters = new StringBuilder();
            Class<?>[] parameterTypes = calculationMethod.getParameterTypes();
            calculationMethodParameters.append("(");
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) calculationMethodParameters.append(", ");

                Class<?> parameterType = parameterTypes[i];
                String parameterName = calculationMethodParameterNames[i];

                calculationMethodParameters.append(parameterType.getCanonicalName());
                calculationMethodParameters.append(" ");
                calculationMethodParameters.append(parameterName);
            }
            calculationMethodParameters.append(")");

            // Drop in supplied source into a skeleton for the generated class
            generatedSource =
                    "\n" +
                    "// Source generated with JaninoClassBuilder: \n" +
                    "package " +
                    GENERATED_CLASS_PACKAGE +
                    ";\n" +
                    sourcesFor(SourceLocation.IMPORTS) +
                    "public final class " +
                    GENERATED_CLASS_NAME +
                    " " +
                    implementsOrExtends +
                    " " +
                    implementedClass.getName() +
                    " {\n" +
                    "  \n" +
                    sourcesFor(SourceLocation.FIELDS) +
                    "  \n" +
                    "  // Constructor\n" +
                    "  public " + GENERATED_CLASS_NAME  + "() {\n" +
                    sourcesFor(SourceLocation.CONSTRUCTOR) +
                    "  }\n" +
                    "  \n" +
                    "  // Calculation method\n" +
                    "  public " +
                    calculationMethod.getReturnType().getCanonicalName() +
                    " " +
                    calculationMethod.getName() +
                    calculationMethodParameters.toString() +
                    " {\n" +
                    sourcesFor(SourceLocation.BEFORE_CALCULATION) +
                    sourcesFor(SourceLocation.AT_CALCULATION) +
                    sourcesFor(SourceLocation.AFTER_CALCULATION) +
                    "  }\n" +
                    "  \n" +
                    sourcesFor(SourceLocation.METHODS) +
                    "}\n\n";

        }

        return generatedSource;
    }

    @Override public Class<? extends T> createClass() throws ClassBuilderException {
        if (generatedClass == null) {
            // Construct source
            final String generatedSource = createSource();

            // Compile it
            try {
                // Compile
                SimpleCompiler janinoCompiler = new SimpleCompiler();
                janinoCompiler.cook(name, generatedSource);

                // Get compiled class
                generatedClass = (Class<? extends T>) janinoCompiler.getClassLoader().loadClass(GENERATED_CLASS_FULL_PATH);
            } catch (CompileException e) {
                throw new ClassBuilderException(e, name,
                                               generatedSource, "There was a compile error in the generated source code.");
            } catch (ClassNotFoundException e) {
                throw new ClassBuilderException(e, name, null, "Could not compile the generated code because a requested class was not found.");
            }
        }

        return generatedClass;
    }

    @Override public T createInstance() throws ClassBuilderException {

        // Compile the generated class
        Class<? extends T> generatedClass = createClass();

        try {
            // Create a new instance of it by calling the constructor
            return generatedClass.newInstance();

        } catch (InstantiationException e) {
            throw new ClassBuilderException(e, name, null, "Could not instantiate the compiled class.");
        } catch (IllegalAccessException e) {
            throw new ClassBuilderException(e, name, null, "Could not access the compiled code.");
        }
    }

    @Override public final String createUniqueIdentifier() {
        return createUniqueIdentifier(null, null);
    }

    @Override public final String createUniqueIdentifier(String prefix) {
        return createUniqueIdentifier(prefix, null);
    }

    @Override public final String createUniqueIdentifier(String prefix, String postfix) {
        if (prefix != null) {
            Check.strictIdentifier(prefix, "prefix");
            Check.lessOrEqual(prefix.length(), "prefix length", MAXIMUM_PREFIX_LENGTH, "maximum prefix length");
            if (prefix.contains("_")) throw new IllegalArgumentException("The prefix should not contain underscores _, but it was " + prefix);
        }
        else {
            prefix = "";
        }

        if (postfix != null) {
            Check.strictIdentifier(postfix, "postfix");
            Check.lessOrEqual(postfix.length(), "postfix length", MAXIMUM_POSTFIX_LENGTH, "maximum postfix length");
            if (postfix.contains("_")) throw new IllegalArgumentException("The postfix should not contain underscores _, but it was " + prefix);
        }

        final long id = nextFreeUniqueId.getAndIncrement();

        return prefix + "_" + id + (postfix == null ? "" : "_" + postfix);
    }


    /**
     * Should be called when anything is added, removed, or modified in the code.
     * Ensures the generated class is recompiled next time it is requested.
     */
    private void codeChanged() {
        generatedSource = null;
        generatedClass = null;
    }

    /**
     * @return all sources added to the specified location, as a string, in the order they were added.
     */
    private String sourcesFor(final SourceLocation location) {
        final String locationSources = sourcePortions.get(location).toString();

        if (locationSources.isEmpty()) {
            return "";
        }
        else {
            return "\n" +
                   location.getIndent() + "// " + location.toString() + "\n" +
                   locationSources +
                   "\n";
        }
    }

    private Method getMethod(Class<T> classToSearch, String methodName) {
        for (Method method : classToSearch.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
