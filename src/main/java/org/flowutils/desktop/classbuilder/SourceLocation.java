package org.flowutils.desktop.classbuilder;

/**
 * Indicates where in the generated source some code should be placed.
 * Used when adding source to SourceBuilder.
 */
public enum SourceLocation {
    IMPORTS(0, false, false),
    FIELDS(1, true, false),
    CONSTRUCTOR(2, true, true),
    METHODS(1, false, false),
    BEFORE_CALCULATION(2, true, true),
    AT_CALCULATION(2, true, true),
    AFTER_CALCULATION(2, true, true)
    ;

    private String indent = "";
    private final boolean validVariableLocation;
    private final boolean validAssignmentLocation;

    SourceLocation(int indentSize, boolean validVariableLocation, boolean validAssignmentLocation) {
        this.validVariableLocation = validVariableLocation;
        this.validAssignmentLocation = validAssignmentLocation;

        for (int i = 0; i < indentSize; i++) {
            indent += "  ";
        }
    }

    public String getIndent() {
        return indent;
    }

    public boolean isValidVariableLocation() {
        return validVariableLocation;
    }

    public boolean isValidAssignmentLocation() {
        return validAssignmentLocation;
    }
}
