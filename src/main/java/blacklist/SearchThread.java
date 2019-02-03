package blacklist;

import java.util.LinkedList;

public class SearchThread extends Thread {
	public int iniSegmento;
	public int finSegmento;
	public String ipaddress;
	public HostBlacklistsDataSourceFacade skds;
	public int BLACK_LIST_ALARM_COUNT;
	public HostBlackListsValidator validator;
	public int numHilo;
	
	public SearchThread(int iniSegmento, int finSegmento, String ipaddress, int numHilo, HostBlacklistsDataSourceFacade skds, int BLACK_LIST_ALARM_COUNT, HostBlackListsValidator validator) throws InterruptedException {
		super("hilo "+numHilo);
		this.iniSegmento = iniSegmento;
		this.finSegmento = finSegmento;
		this.ipaddress = ipaddress;
		this.skds = skds;
		this.BLACK_LIST_ALARM_COUNT = BLACK_LIST_ALARM_COUNT;
		this.validator = validator;
		this.numHilo = numHilo;
	}	

	public void run() {			
		for (int i=iniSegmento; i<=finSegmento && validator.getOcurrencesCount()<BLACK_LIST_ALARM_COUNT; i++) {
			System.out.println(iniSegmento+" - "+finSegmento +" - " +i);
			validator.setCheckedListsCount();          
            if (skds.isInBlackListServer(i, ipaddress)){ 
            	System.out.println("ESTÁ en Thread "+numHilo);
            	validator.setBlackListOcurrences(i);              
                validator.setOcurrencesCount();
            }
        }
	}
	
	
	
}