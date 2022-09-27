package renderEngine;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import engineTester.MainGameLoop;
import toolbox.KeyListener;
import toolbox.MouseListener;


public class Window {
    
    public static int WIDTH, HEIGHT;
    private String title;
    private long glfwWindow;

    private static Window window = null;
    
    private static MainGameLoop MGL;
    
    private static float lastFrameTime;
    private static float deltaTime;
    private int frameCount = 0;

    private Window() {
        Window.WIDTH = 1920;
        Window.HEIGHT = 1080;
        this.title = "Test";
    }

    // Singleton
    public static Window get() {

        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public long getHandle() {
        return this.glfwWindow;
    }
   

    public void run() {

        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
        
        MGL.stop();
       
    }

    public void init() {

        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW 
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // * Must have this line for macOS *

        // Create the Window
        glfwWindow = glfwCreateWindow(Window.WIDTH, Window.HEIGHT, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }

        // Assigns the GLFW callbacks to the respective methods we created in our MouseListener and KeyListener classes
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        /* 
            This line is critical for LWJGL's interoperation with GLFW's
            OpenGL context, or any context that is managed externally.
            LWJGL detects the context that is current in the current thread,
            creates the GLCapabilities instance and makes the OpenGL
            bindings available for use. All other GL functions have to be after this.
        */
        GL.createCapabilities();

        //Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
		glfwShowWindow(glfwWindow);

        // Set the rendering dimensions within the physical window 
//        glViewport(0, 0, Window.WIDTH, Window.HEIGHT);

        // Adjust the viewport when the window size is adjusted so we don't render to unseen pixels
        GLFW.glfwSetFramebufferSizeCallback(Window.get().getHandle(), (window, width, height) -> {
            glViewport(0, 0, Window.WIDTH, Window.HEIGHT);
        });

        
        MGL = new MainGameLoop();
        MGL.start();
       
    }  
 
    public void loop() {

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Check and call events
            glfwPollEvents();
            processInput();
            
            MGL.update();
            
            float currentFrameTime = getCurrentTime();
            deltaTime = (currentFrameTime - lastFrameTime);
            frameCount++;
            if (deltaTime >= 1.0 / 30.0f) {
            	String FPS = "FPS :: " + (int)((1.0 / deltaTime) * frameCount);
            	String ms = "ms :: " + (deltaTime / frameCount) * 1000;
            	String newTitle = "MINI - " + FPS + " / " + ms;
            	glfwSetWindowTitle(glfwWindow, newTitle);
                lastFrameTime = currentFrameTime;
                frameCount = 0;
            }
            
            MouseListener.endFrame();
            
            // Double buffer (front shown, back rendering)
            glfwSwapBuffers(glfwWindow);

        }
        
    }
    
    public static float getCurrentTime() {
    	return (float)GLFW.glfwGetTime();
    }
    
    public static float getFrameTimeSeconds() {
    	return deltaTime;
    }
    
    private void processInput() {
        if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
            glfwSetWindowShouldClose(glfwWindow, true);
        }
    }

    

}
