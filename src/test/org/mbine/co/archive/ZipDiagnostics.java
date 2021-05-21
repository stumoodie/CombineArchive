/**
 * Copyright 2013 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of
 * the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.mbine.co.archive;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Stuart Moodie
 *
 */
public class ZipDiagnostics {
	private static final byte DIR_MATCH_STATES[] = { 0x50, 0x4b, 0x01, 0x02 }; // Signature 0x02014b50
	private int matchIdx;

	public ZipDiagnostics() {
		this.matchIdx = 0;
	}
	
	public void readFile(String fileName){
		try(FileInputStream fis = new FileInputStream(fileName)){
			FileChannel fc = fis.getChannel();
			int sz = (int)fc.size();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			while(bb.hasRemaining()){
				byte currVal = bb.get();
//				System.out.println("buf posn=" + bb.position());
				if(matchedCentralDirectory(currVal)){
					System.out.println("Found central directory at posn=" + bb.position());
					readDirectoryEntry(bb);
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private String getByteArray(byte ... byteArr){
//		StringBuilder buf = new StringBuilder();
//		for(byte byteBuf : byteArr){
//			buf.append(String.format("%02X", byteBuf));
//		}
//		return buf.toString();
//	}
	
	private void readDirectoryEntry(MappedByteBuffer bb) {
		final int jumpToFileName = 20; 
		byte twoBuf[] = new byte[2];
		twoBuf[0] = bb.get();
		twoBuf[1] = bb.get();
//		System.out.println("bytes=" + getByteArray(twoBuf));
		System.out.println("Version made by. Compatibility=" + twoBuf[1] + ", Version=" + twoBuf[0]/10 + "." + twoBuf[0]%10);
		bb.get(twoBuf);
//		System.out.println("bytes=" + getByteArray(twoBuf));
		System.out.println("Version needed to extract. Compatibility=" + twoBuf[1] + ", Version=" + twoBuf[0]/10 + "." + twoBuf[0]%10);
		bb.position(bb.position()+jumpToFileName);
		int  fileLength = bb.getShort();
		bb.position(bb.position()+16);
		byte fNameArr[] = new byte[fileLength];
		bb.get(fNameArr);
		String fileName = new String(fNameArr);
		System.out.println("File name = " + fileName);
	}
	
	public static void main(String[] argv){
		ZipDiagnostics zip = new ZipDiagnostics();
		zip.readFile(argv[0]);
	}

	private boolean matchedCentralDirectory(byte currVal) {
		boolean retVal = false;
		if(currVal == DIR_MATCH_STATES[matchIdx]){
			matchIdx++;
		}
		else{
			matchIdx = 0;
		}
		if(matchIdx == DIR_MATCH_STATES.length){
			retVal = true;
			matchIdx = 0;
		}
		return retVal;
	}

}
