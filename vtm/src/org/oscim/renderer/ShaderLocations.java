package org.oscim.renderer;

/**
 * Class for shader locations builder.
 */
public abstract class ShaderLocations {
    private String directive;
//    protected GLShader shader;

    public ShaderLocations() {

    }

    public ShaderLocations(String directive) {
        this.directive = directive;
    }

    public String getDirective() {
        return directive;
    }

//    public GLShader useMainShader(){
//        return shader;
//    }

    /**
     * Init shader locations
     */
    public abstract void init(GLShader shader);

//    public final void init(GLShader shader) {
//        this.shader = shader;
//        init();
//    }
}
