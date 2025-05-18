package org.code;

import java.rmi.Naming;

public class Server {
    public static void main(String[] args) {
        String hostName = "localhost";
        String serviceName ="Fsy";
        if(args.length == 2){
            hostName = args[0];
            serviceName = args[1];
        }
        try{
            WhiteBoardService service = new WhiteBoardServiceImpl();
            Naming.rebind("rmi://"+hostName+"/"+serviceName, service);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}