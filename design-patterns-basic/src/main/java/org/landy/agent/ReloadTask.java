package org.landy.agent;

import java.io.InputStream;

import java.lang.instrument.ClassDefinition;

import java.lang.instrument.Instrumentation;

/**
 * @author ：MCI10935
 * @date ：Created in 11/9/2020 2:05 PM
 * @description：
 * @version:
 */
public class ReloadTask {
    private  Instrumentation  inst;

    protected  ReloadTask(Instrumentation  inst){

        this.inst=inst;

    }



    public  void  run() {

        try{

            ClassDefinition[]  cd=new ClassDefinition[1];

            Class[]  classes=inst.getAllLoadedClasses();

            for(Class  cls:classes){
                if(cls.getClassLoader()==null||!cls.getClassLoader().getClass().getName().equals("sun.misc.Launcher$AppClassLoader"))
                {continue;}

                String  name=cls.getName().replaceAll("\\.","/");
                byte [] b = loadClassBytes(cls,name+".class");
                if(b!=null){
                    cd[0]=new ClassDefinition(cls,b);
                    try{
                        inst.redefineClasses(cd);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        }catch(Exception ex){

            ex.printStackTrace();

        }

    }

    private  byte[]  loadClassBytes(Class  cls,String  clsname) throws  Exception{
        if(!clsname.startsWith(HotAgent.packages)){
            return null;
        }
        //   System.out.println("class name "+clsname);

        InputStream  is=cls.getClassLoader().getSystemClassLoader().getResourceAsStream(clsname);

        if(is==null)return  null;

        byte[]  bt=new  byte[is.available()];

        is.read(bt);

        is.close();

        return  bt;

    }

}
