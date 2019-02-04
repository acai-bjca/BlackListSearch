/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blacklist;

import java.util.List;
import java.lang.Runtime;

/**
 *
 * @author hcadavid
 */
public class Main {
    
    public static void main(String a[]){
        HostBlackListsValidator hblv=new HostBlackListsValidator();
		try {
			int procesadores = Runtime.getRuntime().availableProcessors();
			System.out.println(procesadores);
			List<Integer> blackListOcurrences = hblv.checkHost("202.24.34.55", 200);
			//200.24.34.55
			//202.24.34.55 No confiable
			//212.24.24.55 Confiable
			System.out.println("Cantidad servidores "+hblv.getCantServidores());
	        System.out.println("The host was found in the following blacklists:"+blackListOcurrences);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
    }    
}
