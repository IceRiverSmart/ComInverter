import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

class Packets
{
	public int		srcaddr;
	public int 		destAddr;
	public int		ctrlCode;
	public int    	funCode;
	public int		dataLen;
	public byte[]	data = new byte[512];
};
class InvInfo
{
	public byte[] model = new byte[16];
	public byte[] serno	= new byte[16];
	int[] chnDescTab = new int[48];
	int[] paraDescTab = new int[32];
	int   dstAddr;
	boolean	phase_3;
};

class ChnData
{
	Calendar ct;
	int 	temp;
	int 	vpv;
	int 	vpv1;
	int 	vpv2;
	int 	vpv3;
	int 	Iac;
	int 	Vac;
	int 	Fac;
	int 	Pac;
	long 	ETotal;			//total energy
	long	htotal;			//total hour
	int		mode;
	long 	etoday;			//today energy
	long	emsg;			//error message
	int		ipv1;
	int		ipv2;
	int		ipv3;
	int		iac[];			//3 phase iac
	int 	vac[];			//3 phase vac
	int		fac[];			//3 phase fac
	int 	pac[];			//3 phase pac
	long	etotal[];		//3 phase etotal
	int		pf;				//power factor
	int 	phase_posing;	//leading or langing
}
public class Inverter {
	
	static final int commIdle 			= 	0x00;
	static final int commConnReq 		=	0x01;
	static final int commRegReq			= 	0x02;
	
	static final int commAddrAssign		= 	0x03;
	static final int commAddrAssRpy		=	0x04;
	static final int commQryChnDesc		=	0x05;
	static final int commQryChnDescRpy	=	0x06;
	static final int commQryParaDesc	=	0x07;
	static final int commQryParaDescRpy	=	0x08;
	static final int commQryChnData		=	0x09;
	static final int commQryChnDataRpy	=	0x0a;
	static final int commQryParaData	=	0x0b;
	static final int commQryParaDataRpy	= 	0x0c;
	
	static final int commQryIDInfo 		= 	0x0f;
	static final int commQryIDInfoRpy	= 	0x10;
	
	static final int commQryFixInfo		=	0x11;
	static final int commQryFixInfoRpy	=	0x90;
	
	static final int commLoginSts		=	0x80;
	static final int commQcDataSts		=	0x81;
	
	static final int ctrlRegister		= 	0x10;
	static final int ctrlRead			=	0x11;
	static final int ctrlWrite			=	0x12;
	static final int ctrlExcute		=	0x13;
	
	static final int packHeader1		= 	0x3a;
	static final int packHeader2		=	0x3a;
	
	static final int srcAddr			= 	0x0100;
	static final int dstAddr			=	0x00;
	
	static final int FUN_00				= 	0x00;
	static final int FUN_01				= 	0x01;
	static final int FUN_02				= 	0x02;
	static final int FUN_03				= 	0x03;
	static final int FUN_04				= 	0x04;
	static final int FUN_05				= 	0x05;
	
	static final int FUN_10				=	0x10;
	
	static final int FUN_80				=	0x80;
	static final int FUN_81				=	0x81;
	static final int FUN_82				=	0x82;
	static final int FUN_83				=	0x83;
	static final int FUN_84				=	0x84;
	
	static final int FUN_90				=	0x90;
	
	static final int recvCommHead1		=	0x01;
	static final int recvCommHead2		=	0x02;
	static final int recvCommSAddrHigh	=	0x03;
	static final int recvCommSAddrLow	= 	0x04;
	static final int recvCommDAddrHigh	=	0x05;
	static final int recvCommDAddrLow	=	0x06;
	static final int recvCommCtrlCode	=	0x07;
	static final int recvCommFunCode	=	0x08;
	static final int recvCommDataLen	=	0x09;
	static final int recvCommData		=	0x0a;
	static final int recvCommChkHigh	=	0x0b;
	static final int recvCommChkLow		=	0x0c;
	
	static final int CHN_TEMP			=	0x00;
	static final int CHN_VPV1			=	0x01;
	static final int CHN_VPV2			=	0x02;
	static final int CHN_VPV3			=	0x03;
	static final int CHN_IPV1			=	0x04;
	static final int CHN_IPV2			=	0x05;
	static final int CHN_IPV3			=	0x06;
	static final int CHN_ETOTAL_H		=	0x07;
	static final int CHN_ETOTAL_L		=	0x08;
	static final int CHN_HTOTAL_H		=	0x09;
	static final int CHN_HTOTAL_L		=	0x0a;
	static final int CHN_PAC			=	0x0b;
	static final int CHN_MODE			=	0x0c;
	static final int CHN_ETODAY			=	0x0d;
	static final int CHN_VPV4			=	0x0e;
	static final int CHN_VPV5			=	0x0f;
	static final int CHN_VPV6			=	0x10;
	static final int CHN_IPV4			=	0x11;
	static final int CHN_IPV5			=	0x12;
	static final int CHN_IPV6			=	0x13;
	
	static final int CHN_EMSG_H			=	0x3e;
	static final int CHN_EMSG_L			=	0x3f;
	
	static final int CHN_VPV_R			=	0x40;
	static final int CHN_IAC_R			=	0x41;
	static final int CHN_VAC_R			=	0x42;
	static final int CHN_FAC_R			=	0x43;
	static final int CHN_PAC_R			=	0x44;
	static final int CHN_ZAC_R			=	0x45;
	static final int CHN_IPV_R			=	0x46;
	static final int CHN_ETOTAL_H_R		=	0x47;
	static final int CHN_ETOTAL_L_R		=	0x48;
	static final int CHN_HTOTAL_H_R		=	0x49;
	static final int CHN_HTOTAL_L_R		=	0x4a;
	static final int CHN_POWER_ON_R		=	0x4b;
	static final int CHN_MODE_R			=	0x4C;
	static final int CHN_EMSG_H_R		=	0x7e;
	static final int CHN_EMSG_L_R		=	0x7f;
	
	static final int CHN_VPV_S			=	0x80;
	static final int CHN_IAC_S			=	0x81;
	static final int CHN_VAC_S			=	0x82;
	static final int CHN_FAC_S			=	0x83;
	static final int CHN_PAC_S			=	0x84;
	static final int CHN_ZAC_S			=	0x85;
	static final int CHN_IPV_S			=	0x86;
	static final int CHN_ETOTAL_H_S		=	0x87;
	static final int CHN_ETOTAL_L_S		=	0x88;
	static final int CHN_HTOTAL_H_S		=	0x89;
	static final int CHN_HTOTAL_L_S		=	0x8a;
	static final int CHN_POWER_ON_S		=	0x8b;
	static final int CHN_MODE_S			=	0x8C;
	static final int CHN_EMSG_H_S		=	0xBe;
	static final int CHN_EMSG_L_S		=	0xBf;
	
	static final int CHN_VPV_T			=	0xc0;
	static final int CHN_IAC_T			=	0xc1;
	static final int CHN_VAC_T			=	0xc2;
	static final int CHN_FAC_T			=	0xc3;
	static final int CHN_PAC_T			=	0xc4;
	static final int CHN_ZAC_T			=	0xc5;
	static final int CHN_IPV_T			=	0xc6;
	static final int CHN_ETOTAL_H_T		=	0xc7;
	static final int CHN_ETOTAL_L_T		=	0xc8;
	static final int CHN_HTOTAL_H_T		=	0xc9;
	static final int CHN_HTOTAL_L_T		=	0xca;
	static final int CHN_POWER_ON_T		=	0xcb;
	static final int CHN_MODE_T			=	0x4C;
	static final int CHN_EMSG_H_T		=	0xfe;
	static final int CHN_EMSG_L_T		=	0xff;
	
	static final int R_PHASE			=	0x00;
	static final int S_PHASE			=	0x01;
	static final int T_PHASE			=	0x02;
	
	

	
	static final int invSerialLen		=	16;
	
	static int MaxInverterTl    		 =	50;
	
	static final int BAUDRATE = 9600;
	static SerialPort serialPort;
	
	static OutputStream outStream;
	static InputStream inStream;
	static int	commSts;
	
	static InvInfo[] inverter = new InvInfo[256];
	static int inverter_tl;
	static InvInfo mCurrentInvPtr;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i;
		boolean res;
		Packets txpack = new Packets();
		Packets rxpack = new Packets();
		InvInfo inv = new InvInfo();
		commSts = commConnReq ;
		//commSts = commLoginSts;
		try {
			openPortName("COM3");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(i=0;i<3;i++)
		{
			ReRegInv(txpack); //clear register status, to try 3 times
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while(true)
		{
			switch(commSts)
			{
//			case commIdle:
//				commSts = CommIdleProc();
//				break;
			case commConnReq:
				txpack.ctrlCode =	ctrlRegister;
				txpack.funCode	=	FUN_00;
				CommSendPackage(txpack);
				commSts = commRegReq;
				break;
			case commRegReq:
				res = RecvCommPacket(rxpack);
				if(true ==  res)
				{
					System.out.println("Comm Register request");
					if(CheckPacket(commRegReq,rxpack))
					{
						commSts = commLoginSts;
					}
				}
				break;
			case commLoginSts:
				System.out.println("comm login status");
				if(commLoginProc(rxpack.data,inv))
				{
					commSts = commQcDataSts;
				}
				break;
			case commQcDataSts:
				if(true == QryChnData(inv.dstAddr,txpack))
				{
					if(true == RecvCommPacket(rxpack))
					{
						if(true==CheckPacket(commQryChnDataRpy,rxpack))
						{
							DecodeChnData(rxpack,inv);
						}
					}
				}
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	static void DecodeChnData(Packets packet,InvInfo inv)
	{
		int chn_tl;
		int i;
		short data;
		int cdata;
		Calendar c = Calendar.getInstance();
		chn_tl = packet.dataLen/2;
		if(chn_tl < 0)
		{
			return;
		}
		for(i=0;i<chn_tl;i++)
		{
			data = (short) ((packet.data[i*2] <<8)|(packet.data[i*2+1]));
			cdata = getUnsignedByte(data);
			DecodeChnDataField(inv.chnDescTab[i],cdata);
		}
	}
	static void DecodeChnDataField(int chnDescTab, int cdata)
	{
		ChnData chnData = new ChnData();
		
		switch(chnDescTab)
		{
			case CHN_TEMP:
				chnData.temp = cdata;
				break;
			case CHN_VPV1:
				chnData.vpv1 = cdata;
				break;
			case CHN_VPV2:
				chnData.vpv2 = cdata;
				break;
			case CHN_VPV3:
				chnData.vpv3 = cdata;
				break;
			case CHN_IPV1:
				chnData.ipv1 = cdata;
				break;
			case CHN_IPV2:
				chnData.ipv2 = cdata;
				break;
			case CHN_IPV3:
				chnData.ipv3 = cdata;
				break;
			case CHN_ETODAY:
				chnData.etoday = cdata;
				break;
			case CHN_VPV_R:
				chnData.vpv = cdata;
				break;
			case CHN_IAC_R:
				chnData.iac[R_PHASE] = cdata;
				break;
			case CHN_IAC_S:
				chnData.iac[S_PHASE] = cdata;
				break;
			case CHN_IAC_T:
				chnData.iac[T_PHASE] = cdata;
				break;
			case CHN_VAC_R:
				chnData.vac[R_PHASE] = cdata;
				break;
			case CHN_VAC_S:
				chnData.vac[S_PHASE] = cdata;
				break;
			case CHN_VAC_T:
				chnData.vac[T_PHASE] = cdata;
				break;
			case CHN_PAC_R:
				chnData.pac[R_PHASE] = cdata;
				break;
			case CHN_PAC_S:
				chnData.pac[S_PHASE] = cdata;
				break;
			case CHN_PAC_T:
				chnData.pac[T_PHASE] = cdata;
				break;
			case CHN_FAC_R:
				chnData.fac[R_PHASE] = cdata;
				break;
			case CHN_FAC_S:
				chnData.fac[S_PHASE] = cdata;
				break;
			case CHN_FAC_T:
				chnData.fac[T_PHASE] = cdata;
				break;
			case CHN_PAC:
				chnData.Pac = cdata;
				break;
			case CHN_ETOTAL_H:
				chnData.ETotal |= ((cdata & 0xFFFF)<<16);
				break;
			case CHN_ETOTAL_L:
				chnData.ETotal |= (cdata & 0xFFFF);
				break;
			case CHN_ETOTAL_H_R:
				chnData.etotal[R_PHASE] |= ((cdata & 0xFFFF)<<16);
				break;
			case CHN_ETOTAL_L_R:
				chnData.etotal[R_PHASE] |= (cdata & 0xFFFF);
				break;
			case CHN_ETOTAL_H_S:
				chnData.etotal[S_PHASE] |= ((cdata & 0xFFFF)<<16);
				break;
			case CHN_ETOTAL_L_S:
				chnData.etotal[S_PHASE] |= (cdata & 0xFFFF);
				break;
			case CHN_ETOTAL_H_T:
				chnData.etotal[T_PHASE] |= ((cdata & 0xFFFF)<<16);
				break;
			case CHN_ETOTAL_L_T:
				chnData.etotal[T_PHASE] |= (cdata & 0xFFFF);
				break;
			case CHN_HTOTAL_H:
				chnData.htotal |= ((cdata & 0xFFFF)<<16);
				break;
			case CHN_HTOTAL_L:
				chnData.htotal |= (cdata & 0xFFFF);
				break;
			case CHN_MODE:
				chnData.mode = cdata;
				break;
			case CHN_EMSG_H:
				chnData.emsg = ((cdata & 0xFFFF)<<16);
				break;
			case CHN_EMSG_L:
				chnData.emsg |= (cdata & 0xFFFF);
				break;
				
		}
		
	}
	static boolean commLoginProc(byte[] serno,InvInfo inv)
	{
		int commLoginSts = commAddrAssign;
		//int commLoginSts = commQryIDInfo;
		Packets txpack =  new Packets();
		Packets rxpack =  new Packets();
		int i=0;
		
		byte devType = 0;
		
		while(true)
		{
			switch(commLoginSts)
			{
			case commAddrAssign:
				txpack.data = serno;
				if(true == AssignDestAddr(txpack,inv))
				{
					commLoginSts = commAddrAssRpy;
				}
				else
				{
					return false;
				}
				break;
			case commAddrAssRpy:
				if(true == RecvCommPacket(rxpack))
				{
					if(true == CheckPacket(commAddrAssRpy,rxpack))
					{
						//commLoginSts = commQryIDInfo;
						commLoginSts = commQryFixInfo;//direct get data to use fixed szie
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				break;
			case commQryFixInfo:
				if(true ==QryFixInfo(inv.dstAddr,txpack))
				{
					commLoginSts = commQryFixInfoRpy;
				}
				else
				{
					return false;
				}
				break;
			case commQryFixInfoRpy:
				if(RecvCommPacket(rxpack))
				{
					if(true==CheckPacket(commQryFixInfoRpy,rxpack))
					{
						DecodeFixedData(rxpack);
					}
					
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				break;
			case commQryIDInfo:
				if(true == QryIDInfo(inv.dstAddr,txpack))
				//if(QryInfo(inv.dstAddr, txpack, ctrlRead,FUN_03 ))
				{
					commLoginSts = commQryIDInfoRpy;
				}
				else
				{
					return false;
				}
				break;
			case commQryIDInfoRpy:
				if(RecvCommPacket(rxpack))
				{
					if(true==CheckPacket(commQryIDInfoRpy,rxpack))
					{
						if(rxpack.data[0] == 0x33)//3 phase model
						{
							inv.phase_3 = true;
						}
						else
						{
							inv.phase_3 = false;
						}
					
						System.arraycopy(rxpack.data, 12, inv.model, 0, invSerialLen);
						System.arraycopy(rxpack.data, 44, inv.serno, 0, invSerialLen);
						commLoginSts = commQryChnDesc;
					}
					
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
				break;
			case commQryChnDesc:
				//if(QryInfo(inv.dstAddr, txpack, ctrlRead,FUN_00 ))
				if(true == QryChnDesc(inv.dstAddr,txpack))
                {
                    //DEBUGMSG(1, (L"COMM_QRY_CHN_DESC OK\n"));
                    commLoginSts = commQryChnDescRpy;
                }
				break;
			case commQryChnDescRpy:
				if(true == RecvCommPacket(rxpack))
				{
					if(true==CheckPacket(commQryChnDescRpy,rxpack))
					{
						DecodeChnDesc(rxpack,inv);
						return true;
					}
				}
				else
				{
					return false;
				}
				break;
			case commQryParaDesc:
				break;
			case commQryParaDescRpy:
				break;
			case commQryParaData:
				break;
			case commQryParaDataRpy:
				break;
				
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	//3A 3A :protocol header->2 byte
	//00 01 :src addr->2 byte
	//01 00 :dst addr->2 byte
	//11 :control code->1 byte
	//90 :function code->1 byte
	//42 :data length->1 byte
	//01 96 :temperature->2 byte
	//08 7C :vpv1->2 byte
	//00 00 :vpv2->2 byte
	//FF FF :NA
	//00 05:ipv1->2 byte
	//00 B4:ipv2->2 byte
	//FF FF:NA
	//00 04:Iac-R->2 byte
	//FF FF:Iac-S->2 byte
	//FF FF:Iac-T->2 byte
	//08 E6:Vac-R->2 byte
	//FF FF:Vac-S->2 byte
	//FF FF:Vac-T->2 byte
	//13 8D:Fac
	//00 61:Pac-R->2 byte
	//FF FF:Pac-S->2 byte
	//FF FF:Pac-T->2 byte
	//FF FF: NA->2 byte
	//FF FF: NA->2 byte
	//FF FF: EToday->2 byte
	//00 00:ETotal-H->2 byte
	//02 1D:ETotal-L->2 byte
	//00 00:HTotal-H->2 byte
	//02 33:HTotal-H->2 byte 
	//00 01:mode->1 byte
	//00 00:GVFaultValue->2 byte
	//00 00:GFFaultValue->2 byte 
	//FF FF:GZFaultValue->2 byte 
	//00 00:TmpFaultValue->2 byte 
	//00 00:PVFaultValue->2 byte 
	//00 00:GFCIFaultValue->2 byte 
	//00 00:Error message high->2 byte 
	//00 00:Error message low->2 byte 
	//1D 5D:chksum
	static void DecodeFixedData(Packets packet)
	{
		ChnData cdata = new ChnData();
		cdata.temp = (packet.data[0]<<8|packet.data[1]);
	}
	static void DecodeChnDesc(Packets packet,InvInfo inv)
	{
		int chn_tl;
		int i;
		chn_tl = packet.dataLen;
		
		for(i=0;i<chn_tl;i++)
		{
			inv.chnDescTab[i] = packet.data[i];
		}
		
	}
	static boolean QryInfo(int destAddr, Packets packet, int funcode,int ctrlcode )
	{
		packet.funCode = funcode;
		packet.ctrlCode = ctrlcode;
		packet.dataLen = 0x00;
		packet.destAddr = destAddr;
		CommSendPackage(packet);
		return true;
		
	}
	static boolean QryFixInfo(int destAddr, Packets packet)
	{
		packet.ctrlCode = ctrlRead;
		packet.funCode = FUN_10;
		packet.dataLen = 0x00;
		packet.destAddr = destAddr;
		
		CommSendPackage(packet);
		return true;
	}
	static boolean QryIDInfo(int destAddr, Packets packet)
	{
		packet.ctrlCode = ctrlRead;
		packet.funCode = FUN_10;
		packet.dataLen = 0x00;
		packet.destAddr = destAddr;
		
		CommSendPackage(packet);
		return true;
	}
	static boolean QryChnDesc(int destAddr, Packets packet)
	{
		packet.ctrlCode = ctrlRead;
		packet.funCode = FUN_00;
		packet.dataLen = 0x00;
		packet.destAddr = destAddr;
		
		CommSendPackage(packet);
		return true;
		
	}
	static boolean QryChnData(int destAddr, Packets packet)
	{
		packet.funCode = FUN_02;
		packet.ctrlCode = ctrlRead;
		packet.dataLen = 0x00;
		packet.destAddr = destAddr;
		CommSendPackage(packet);
		return true;
	}
	static boolean AssignDestAddr(Packets packet, InvInfo pinv)
	{
		byte[] sno = new byte [invSerialLen];
		boolean ans = false;
		int i = 0;
		int k = 0;
		int destAddr = 0;
		
		Random random = new Random();
		
		packet.ctrlCode = ctrlRegister;
		packet.funCode  = FUN_01;
		packet.dataLen  = invSerialLen + 1;
		packet.destAddr =  dstAddr;
		
		System.arraycopy(packet.data, 0, sno, 0, invSerialLen);
		
		for(i=0; i< inverter_tl;i++)
		{
			if(Arrays.equals(packet.data, inverter[i].serno))
			{
				packet.data[invSerialLen] = (byte)(inverter[i].dstAddr);
				mCurrentInvPtr = inverter[i];
				ans = true;
				break;
			}
		}
		
		//never connect before
		if(i==inverter_tl)
		{
			if(inverter_tl < MaxInverterTl)
			{
				//if you only a inverter and assign a stable address
				
				
				packet.data[invSerialLen] = 0x01;
				//assign between 0~254 address
//				packet.data[invSerialLen] = (byte) (Math.abs(random.nextInt())%127);
//				while(true)
//				{
//					if(true == CheckAssigDestAddr(destAddr))
//					{
//						packet.data[invSerialLen] = (byte) (Math.abs(random.nextInt())%127);
//					}
//					else
//					{
//						break;
//					}
//				}
				pinv.dstAddr = packet.data[invSerialLen];
				System.arraycopy(packet.data, 0, pinv.serno, 0, invSerialLen);
				ans = true;
			}
			else
			{
				ans = false;
			}
			
		}
		if(ans)
		{
			 CommSendPackage(packet);
			 return true;
		}
		else
		{
			return false;
		}
		
	}
	static boolean CheckAssigDestAddr(int destAddr)
	{
		int tempdestadd = 0;
		int k = 0;
		tempdestadd = destAddr;
		
		for(k =0; k< inverter_tl;k++)
		{
			if(tempdestadd == inverter[k].dstAddr)
			{
				return true;
			}
		}
		return false;
	}
	/* check communication type */
	static boolean CheckPacket(int type,Packets packet)
	{
	    boolean rval = false;

	    if( packet.destAddr != srcAddr ) // Not for me
	    {
	        return false;
	    }

	    switch( type ) 
	    {
	        case commQryChnDataRpy:
	            	if((packet.ctrlCode == ctrlRead) && (packet.funCode == FUN_82))
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	            
	        case commRegReq:
	            if((packet.ctrlCode == ctrlRegister) && (packet.funCode == FUN_80))
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	            
	        case commAddrAssRpy :
	            //printf( "p_pack->ctrl_code = %02x  p_pack->fun_code = %02x!!\n",p_pack->ctrl_code,p_pack->fun_code );
	            if((packet.ctrlCode == ctrlRegister ) && (packet.funCode == FUN_81) && (packet.data[0]== 0x06))
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	            
	        case  commQryIDInfoRpy:
	            if( (packet.ctrlCode== ctrlRead) && (packet.funCode == FUN_83) )
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	            
	        case commQryChnDescRpy:
	            if((packet.ctrlCode == ctrlRead) && (packet.funCode == FUN_80))
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	            
	        case commQryParaDescRpy:
	            if((packet.ctrlCode == ctrlRead) && (packet.funCode == FUN_81)){
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	            
	        case commQryParaDataRpy:
	            if((packet.ctrlCode == ctrlRead)&&(packet.funCode == FUN_84))
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	        case commQryFixInfoRpy:
	            if((packet.ctrlCode == ctrlRead)&&(packet.funCode == FUN_90))
	            {
	                rval = true;
	            }
	            else
	            {
	                rval = false;
	            }
	            break;
	   
	    }
	    
	    return rval;
	}
//
//	static int CommIdleProc()
//	{
//		int commSts = commIdle;
//		if( true == IsInverterRemov)
//	}
	static boolean RecvCommPacket(Packets rxPack)
	{
		byte[] oRecvData = new byte[1];
		int tmp = 0;
		int recvlen = 0;
		byte csts = recvCommHead1;
		
		int chksum = 0;
		int n = 0;
		
		while(true)
		{
			try {
				if (inStream.available() > 0) {
					if(inStream.read(oRecvData, 0, 1)<0)
					{
						return false;
					}
				}else
				{
					return false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			switch(csts)
			{
				case recvCommHead1:
					if((oRecvData[0]&0xFF) == packHeader1)
					{
						csts = recvCommHead2;
						chksum = oRecvData[0]&0xFF;
					}
					break;
				case recvCommHead2:
					if((oRecvData[0]&0xFF) == packHeader2)
					{
						csts = recvCommSAddrHigh;
						chksum += oRecvData[0]&0xFF;
					}
					else
					{
						csts = recvCommHead1;
					}
					break;
				case recvCommSAddrHigh:
					tmp = (int) (oRecvData[0]&0xFF);
					csts = recvCommSAddrLow;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommSAddrLow:
					rxPack.srcaddr =(int) (( tmp << 8) | (oRecvData[0]&0xFF));
					csts = recvCommDAddrHigh;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommDAddrHigh:
					tmp = (int) (oRecvData[0]&0xFF);
					csts = recvCommDAddrLow;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommDAddrLow:
					rxPack.destAddr = (int) (tmp << 8 | (oRecvData[0]&0xFF));
					csts = recvCommCtrlCode;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommCtrlCode:
					rxPack.ctrlCode = (int) (oRecvData[0]&0xFF);
					csts = recvCommFunCode;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommFunCode:
					rxPack.funCode = (int) (oRecvData[0]&0xFF);
					csts = recvCommDataLen;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommDataLen:
					rxPack.dataLen = (int) (oRecvData[0]&0xFF);
					csts = recvCommData;
					recvlen = 0;
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommData:
					rxPack.data[recvlen++] = (byte) (oRecvData[0]&0xFF);
					if(recvlen == rxPack.dataLen)
					{
						csts = recvCommChkHigh;
					}
					chksum += oRecvData[0]&0xFF;
					break;
				case recvCommChkHigh:
					tmp = (int) (oRecvData[0]&0xFF);
					csts = recvCommChkLow;
					break;
				case recvCommChkLow:
					int rxchksum = (tmp << 8) | oRecvData[0]&0xFF;
					if(chksum == rxchksum)
					{
						return true;
					}
					else
					{
						csts = recvCommHead1;
					}
					break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static void ReRegInv(Packets packet)
	{
		int tmp;
		
		//tmp = packet.destAddr;
		
		packet.destAddr	= dstAddr;	//for all inverter
		packet.ctrlCode = ctrlRegister;
		packet.funCode	= FUN_04;
		packet.dataLen	= 0x00;
		//packet.destAddr	= dstAddr;
		
		CommSendPackage(packet);
		
		//packet.destAddr = tmp;

	}
	static void CommSendPackage(Packets packet)
	{
		int[] pbuf = new int[512];
		int packlen;
		int sendlen;
		
		packlen = FmtCommPacket(packet,pbuf);
		
		for(int h=0; h < packlen; h++)
		{
			write(pbuf[h]);
		}
	}
	static void write(int what) {
		try {
			outStream.write(what & 0xff);
			outStream.flush();
		} catch (Exception e) {
			System.err.print("Error when writing to serial port.");
		}
	}
	static int FmtCommPacket(Packets packet, int[] pbuf)
	{
		int chksum = 0;
		int 	len;
		int i;
		
		pbuf[0]	=	packHeader1;			//first 0 byes:0x3a
		pbuf[1] = 	packHeader2;			//first 1 bytes:0x3a
		pbuf[2] =	(srcAddr & 0xFF00) >>8;	//src addr high
		pbuf[3] =	srcAddr & 0xFF;
		pbuf[4] =	(int)((packet.destAddr & 0xFF00) >>8);
		pbuf[5] =	(int)(packet.destAddr & 0xFF);
		pbuf[6] =	packet.ctrlCode;
		pbuf[7] =	packet.funCode;
		pbuf[8] =	packet.dataLen;
		
		
		if(packet.dataLen > 0)
		{
			for(i=0;i<packet.dataLen;i++)
			{
				pbuf[9+i] =	packet.data[i];
			}
			//System.arraycopy((int)packet.data, 0, pbuf, 9, packet.dataLen);
		}
		
		len 	=	packet.dataLen + 9;
		for(i=0;i<len;i++)
		{
			chksum +=pbuf[i];
		}
		
		pbuf[len++]	=	(int)((chksum &0xFF00)>>8);
		pbuf[len++] = 	(int)(chksum & 0xFF);
		return len;
	}
	
	static void openPortName(String portName) throws IOException {
		try {
			
			CommPortIdentifier portId = CommPortIdentifier
					.getPortIdentifier(portName);
			serialPort = (SerialPort) portId.open(getClassNameForStatic(), 5000);
			setSerialPortParameters();
			serialPort.notifyOnDataAvailable(true);
			outStream = serialPort.getOutputStream();
			inStream = serialPort.getInputStream();
			if (inStream.available() > 0)
				for (; inStream.available() > 0; inStream.read())
					;
		} catch (NoSuchPortException e) {
			System.err.println((new StringBuilder("The port: "))
					.append(portName).append(" doesn't exists.").toString());
		} catch (PortInUseException e) {
			System.err.println((new StringBuilder("The port: "))
					.append(portName)
					.append(" is already been used by other program.")
					.toString());
		} catch (IOException e) {
			serialPort.close();
			System.err.println((new StringBuilder("Error opening the port: "))
					.append(portName).toString());
		}
	}
	static void setSerialPortParameters() {
		try {
			serialPort.setSerialPortParams(BAUDRATE, 8, 1, 0);
			serialPort.setFlowControlMode(0);
		} catch (UnsupportedCommOperationException ex) {
			System.err.print("Error when setting serial port parameters.");
		}
	}
	static void close() {
		if (serialPort != null) {
			try {
				outStream.close();
				inStream.close();
			} catch (IOException ex) {
				System.err.println("Error when closing the serial port");
			}
			serialPort.close();
		}
	}
	 private static final String getClassNameForStatic() {  
	       return new Object() {  
	            public String getClassName() {      
	                String className = this.getClass().getName();      
	                return className.substring(0, className.lastIndexOf('$'));      
	            }    
	       }.getClassName();  
	    }   
	 
	 static int getUnsignedByte (byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
		    return data&0x0FF ;
		 }

		static int getUnsignedByte (short data){      //将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
		       return data&0x0FFFF ;
		 }       

		static long getUnsignedIntt (int data){     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
		    return data&0x0FFFFFFFF ;
		 }

}
