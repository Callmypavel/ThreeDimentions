package peacemaker.threedimentions;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 请叫我保尔 on 2015/12/13.
 */
public class ModelLoader {
    public ModelObject LoadModel(String fileName,Context context){
        String line;
        try {
//            Log.v("ModelLoader---------->","LoadModel()检查上下文"+context);
//            Log.v("ModelLoader---------->","LoadModel()检查文件名"+fileName);
//            Log.v("ModelLoader---------->","LoadModel()检查私有资源"+context.getResources().getAssets());
            InputStream inputStream = context.getResources().getAssets().open(fileName);
//            Log.v("ModelLoader---------->","LoadModel()IO异常位置1");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//            Log.v("ModelLoader---------->","LoadModel()IO异常位置2");
            ModelObject modelObject = new ModelObject();
            while ((line = bufferedReader.readLine()) != null) {
               // System.out.println(line);
                String[] elements  = line.split(" ");
                float[] floats;
                int[] ints;
//                Log.v("ModelLoader---------->","LoadModel()头标检查"+elements[0]);
                switch (elements[0]){
                    case "v":
                        floats = new float[3];
                        floats[0] = Float.parseFloat(elements[1]);
                        floats[1] = Float.parseFloat(elements[2]);
                        floats[2] = Float.parseFloat(elements[3]);
//                        Log.v("ModelLoader---------->","LoadModel()顶点检查"+floats);
                        modelObject.addv(floats);
                        break;
                    case "vn":
                        floats = new float[3];
                        floats[0] = Float.parseFloat(elements[1]);
                        floats[1] = Float.parseFloat(elements[2]);
                        floats[2] = Float.parseFloat(elements[3]);
                        modelObject.addvn(floats);
                        break;
                    case "vt":
                        floats = new float[2];
                        floats[0] = Float.parseFloat(elements[1]);
                        floats[1] = Float.parseFloat(elements[2]);
                        modelObject.addvt(floats);
                        break;
                    case "f":
                        ints = new int[9];
                        for(int i = 0;i <= 2;i++){
                            String[] strings = elements[i+1].split("\\/");
                            ints[3*i] = Integer.parseInt(strings[0]);
                            ints[3*i+1] = Integer.parseInt(strings[1]);
                            ints[3*i+2] = Integer.parseInt(strings[2]);
                        }
                        modelObject.addf(ints);
                        break;
                }
            }
            inputStream.close();
//            Log.v("ModelLoader---------->", "LoadModel()检查返回值"+modelObject);
            return modelObject;
        }catch (IOException e){
            Log.v("ModelLoader---------->","LoadModel()IO异常");
        }
        return null;
    }
}
