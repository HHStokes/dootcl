package dootcl.scenes;

import dootcl.Window;
import dootcl.listeners.KeyListener;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    private String vertexShaderSrc = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";
    private int vertexID, fragmentID, shaderProgram;
    private int vaoID, vboID,eboID;
    private float[] vertexArray = {
        //position                   //color
            0.5f, -0.5f, 0.0f,      1.0f, 0.0f, 0.0f, 1.0f,//bottom right 0
            -0.5f, 0.5f, 0.0f,      0.0f, 1.0f, 0.0f, 1.0f,//top left     1
            0.5f, 0.5f, 0.0f,       0.0f, 0.0f, 1.0f, 1.0f,//top right    2
            -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f,//bottom left  3
    };
    //MUST BE IN COUNTER-CLOCKWISE ORDER
    private int[] elementArray = {
        2,1,0,
        0,1,3
    };
    public LevelEditorScene(){

    }
    @Override public void init(){
        //compile and link the shaders

        // load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //pass shader to gpu
        glShaderSource(vertexID,vertexShaderSrc);
        glCompileShader(vertexID);
        //check for errors
        int vertexSuccess = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (vertexSuccess== GL_FALSE) {
            int len = glGetShaderi(vertexID,GL_INFO_LOG_LENGTH);
            System.out.println("Error: defaultShader.glsl\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false : "";
        }

        // load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //pass shader to gpu
        glShaderSource(fragmentID,fragmentShaderSrc);
        glCompileShader(fragmentID);
        //check for errors
        int fragmentSuccess = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (fragmentSuccess == GL_FALSE) {
            int len = glGetShaderi(fragmentID,GL_INFO_LOG_LENGTH);
            System.out.println("Error: defaultShader.glsl\n\tFragment shader compilation failed: ");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false : "";
        }

        //link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram,vertexID);
        glAttachShader(shaderProgram,fragmentID);
        glLinkProgram(shaderProgram);

        //check for linking errors
        int shaderSuccess = glGetProgrami(shaderProgram,GL_LINK_STATUS);
        if (shaderSuccess == GL_FALSE){
            int len = glGetProgrami(shaderProgram,GL_INFO_LOG_LENGTH);
            System.out.println("Error: defaultShader.glsl\n\tLinking of shaders failed");
            System.out.println(GL20C.glGetShaderInfoLog(shaderProgram,len));
            assert false : "";
        }

        //generate VAO, VBO, and EBO buffer objects and send to the GPU
        vaoID = glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        //create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create VBO
        vboID = glGenBuffers();
        GL20.glBindBuffer(GL_ARRAY_BUFFER, vboID);
        GL20.glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        //create EBO
        eboID = GL20.glGenBuffers();
        GL20.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        GL20.glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //Add vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = Float.BYTES;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        GL20.glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        GL20.glEnableVertexAttribArray(1);

        GL11.glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT,0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
        GL20.glUseProgram(0);

    }

    @Override
    public void update(float dt) {
        // bind shader program
        GL20.glUseProgram(shaderProgram);
        // bind the VAO that we are using
        GL30.glBindVertexArray(vaoID);

        //Enable the vertex attribute
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES,elementArray.length, GL_UNSIGNED_INT, 0);

        //UNBIND EVERYTHING
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glUseProgram(0);
    }
}
