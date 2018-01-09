package com.seb.networkTopology.generic;

public class AntennaPort {
	
	private String _antennaPortFDN;
	private String _azimuth;
	
	
	public AntennaPort(String EnbEquipmentId, String cpriRadioEquipmentId, String antennaPortId) {
//		_antennaPortFDN = "ENBEquipment/" + EnbEquipmentId + " CpriRadioEquipment/" + cpriRadioEquipmentId + " AntennaPort/" + antennaPortId;
		_antennaPortFDN = "CpriRadioEquipment/" + cpriRadioEquipmentId + " AntennaPort/" + antennaPortId;
	}
	
	public String getAntennaPortFDN() {
		return _antennaPortFDN;
	}
	
	public void setAzimuth(String azimuth) {
		_azimuth = azimuth;
	}
	
	public String getAzimuth() {
		return _azimuth;
	}
}
