/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blacklist;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {
	private int checkedListsCount=0;
	private String ipaddress;
	private int ocurrencesCount=0;
	LinkedList<Integer> blackListOcurrences;
	private int cantSer;
	private  HostBlacklistsDataSourceFacade skds;

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     * @throws InterruptedException 
     */
    public List<Integer> checkHost(String ipaddress, int cantidadThread) throws InterruptedException{
        skds = HostBlacklistsDataSourceFacade.getInstance();
        blackListOcurrences = new LinkedList();
        this.ipaddress = ipaddress;
        
        int cantServers = skds.getRegisteredServersCount();
        cantSer = cantServers;
        int listasPorThread = cantServers / cantidadThread; 
        int segmento = 0;
        SearchThread[] hilos = new SearchThread[cantidadThread];
        for(int h = 0; h<cantidadThread; h++) {
        	if(h == cantidadThread-1) {
        		//System.out.println("segmento : "+segmento+ " - "+(cantServers-1));
        		hilos[h] = new SearchThread(segmento, cantServers-1, ipaddress, h, skds, BLACK_LIST_ALARM_COUNT, this);
        	} else {
        		//System.out.println("segmento : "+segmento+ " - "+(segmento+listasPorThread-1));
        		hilos[h] = new SearchThread(segmento, segmento+listasPorThread-1, ipaddress, h, skds, BLACK_LIST_ALARM_COUNT, this);
        	}
        	segmento += listasPorThread;        	
        }
        
        //Primero creamos todos los hilos y ejecutamos su método run, de manera que corran en paralelo.
    	for (SearchThread h : hilos) {
    		h.start();
    	}
    	//A todos los hilos ejecutamos el método join, para que el hilo principal no termine si algún hilo falta por terminar.
    	for (SearchThread h : hilos) {
    		h.join();
    	}
    	
    	if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
    	else{
            skds.reportAsTrustworthy(ipaddress);
        }                
        
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }

    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    public synchronized int getOcurrencesCount(){
    	return ocurrencesCount;
	}
    
    public synchronized void setOcurrencesCount(){    	
    	ocurrencesCount++;
	}
    
    public synchronized void setCheckedListsCount(){
    	checkedListsCount++;
	}
	
	public synchronized void setBlackListOcurrences(Integer i){
		blackListOcurrences.add(i);
	}
	
	public int getCantServidores(){
		return cantSer;
	} 
}
