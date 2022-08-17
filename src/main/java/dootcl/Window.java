package dootcl;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Window {
    private int  width, height;
    private String title;
    private long glfwWindow;
    private static Window window = null;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Doot ver 0.0.1";
    }

    public static Window get(){
        if (Window.window == null){
            Window.window = new Window();
        }
        return Window.window;
    }

    public void init(){
        // setup error calllback
        GLFWErrorCallback.createPrint(System.err).set();

        //initilage GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //configuring glfw
        glfwDefaultWindowHints(); //optional
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window is not visible until we are done making it
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window is resizeable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // create the window
        this.glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if(this.glfwWindow == NULL){
            throw new RuntimeException("GLFW failed to create a new window");
        }

        // Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //enable v-sync (buffer swapping)
        glfwSwapInterval(1);

        // make the window visible
        glfwShowWindow(glfwWindow);

        GL.createCapabilities();
    }
    public void loop(){
        while(!glfwWindowShouldClose(this.glfwWindow)){
            // Poll events
            glfwPollEvents();
            glClearColor(0.14f, 0.10f, 0.19f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            glfwSwapBuffers(this.glfwWindow);
        }
    }

    public void run(){
        System.out.println("Window is trying to run. LWJGL version " + Version.getVersion());
        init();
        loop();
    }

}