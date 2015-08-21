package org.flowutils.desktop.classbuilder;

/**
 * Used to build up source for a class, and then compiling the source and getting the compiled class or an instance of it.
 *
 * @param <T> a type that the generated class implements or extends.
 */
// TODO: Support arbitrary method buildup, make source location not an enum, instead have it contain method name and place in it, or such.
// TODO  Add static construction methods to it for locations such as fields, imports, constructor, etc.
public interface ClassBuilder<T> {

    /**
     * Adds the specified line to the source at the specified location.
     * Indentation and terminating newline will be added to the line.
     */
    void addSourceLine(SourceLocation location, String line);

    /**
     * Adds a source line that imports the specified class.
     * Ignores imports of primitives, and imports the component type of array types.
     */
    void addImport(Class type);

    /**
     * Constructs the source from the currently added source lines if they have changed since the last call, and returns it.
     */
    String createSource();

    /**
     * Compiles the current code if it has changed since the last call, and returns the compiled class.
     *
     * @return class representing the compiled code.
     * @throws ClassBuilderException if there was any problem compiling the code.
     */
    Class<? extends T> createClass() throws ClassBuilderException;

    /**
     * Compiles the current code if it has changed since the last call,
     * and returns an instance of the compiled class, using a no-arguments constructor.
     *
     * @return new instance of the generated class.
     * @throws ClassBuilderException if there was any problem compiling the code or creating the instance.
     */
    T createInstance() throws ClassBuilderException;

    /**
     * @return a new unique java style identifier.
     *         Each call creates a new identifier that has not been returned earlier by this ClassBuilder instance.
     */
    String createUniqueIdentifier();

    /**
     * @param prefix a valid java identifier string to prepend to the unique identifier, or null for default.  May not contain underscores, and should not be too long (over 128 chars).
     * @return a new unique java style identifier.
     *         Each call creates a new identifier that has not been returned earlier by this ClassBuilder instance.
     */
    String createUniqueIdentifier(String prefix);

    /**
     * @param prefix a valid java identifier string to prepend to the unique identifier, or null for default.  May not contain underscores, and should not be too long (over 128 chars).
     * @param postfix a valid java identifier string to append to the identifier, or null for default.  May not contain underscores, and should not be too long (over 128 chars).
     * @return a new unique java style identifier.
     *         Each call creates a new identifier that has not been returned earlier by this ClassBuilder instance.
     */
    String createUniqueIdentifier(String prefix, String postfix);
}
