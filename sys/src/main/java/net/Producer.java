package net;

import Utils.Utils;
import net.named_data.jndn.*;
import net.named_data.jndn.encoding.EncodingException;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.KeyType;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;
import net.named_data.jndn.util.Blob;
import sys.Configure;
import sys.Block;


import java.io.IOException;

public class Producer implements OnInterestCallback, OnRegisterFailed, Runnable{

	private Face face_;
	private KeyChain keyChain_;
	private Name certificateName_;
	private Block block;
	private String prefix;
	private int responseCount_ = 0;
		
	public Producer(Block block, String prefix, KeyChain keyChain, Name certificateName){
		this.block = block;
		this.prefix = prefix;
		keyChain_ = keyChain;
		certificateName_ = certificateName;
	}
	
	public Producer(Block block, String prefix){
		this.block = block;
		this.prefix = prefix;
		buildKeyChain();
		
	}
	
	public String getName(){
		return prefix;
	}
	
	public void buildKeyChain() {
		MemoryIdentityStorage identityStorage = new MemoryIdentityStorage();
	    MemoryPrivateKeyStorage privateKeyStorage = new MemoryPrivateKeyStorage();
	    IdentityManager identityManager = new IdentityManager(identityStorage, privateKeyStorage);
	    KeyChain keyChain = new KeyChain(identityManager); 
	    // TODO 锟斤拷锟斤拷每台锟斤拷锟斤拷锟角诧拷一锟斤拷锟侥ｏ拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷莶锟斤拷锟斤拷锟� 
	    //keyName: different between nodes.
	    Name keyName = new Name("/one/DSK-123");
	    // The public key name will be broken into two parts:
	    // The first part (锟斤拷authoritative namespace锟斤拷) will be put before a name component KEY which serves as an application tag
	    // The second part (锟斤拷label锟斤拷) will be put between KEY and ID-CERT which serves as an indicator of certificate.
		Name certificateName = keyName.getSubName(0, keyName.size() - 1).append("KEY").append(keyName.get(-1))
				.append("ID-CERT").append("0");
		try {
			identityStorage.addKey(keyName, KeyType.RSA, new Blob(Configure.getPublicKey(), false));
			privateKeyStorage.setKeyPairForKeyName(keyName, KeyType.RSA, Configure.getPublicKey(),
				Configure.getPrivateKey());
		} catch (SecurityException e) {
			System.out.println("exception: " + e.getMessage());
		} 
	    keyChain_ = keyChain;
	    certificateName_ = certificateName;
	 }


	public void onRegisterFailed(Name prefix) {
		 ++responseCount_;
	      System.out.println("Register failed for prefix " + prefix.toUri());
	}

	/*
	 * receive interest packet.
	 * create data packet.
	 * return data packet.
	 */
	public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId, InterestFilter filter) {
		++responseCount_;
		 Data data = new Data(interest.getName());
		 Blob content = new Blob(Utils.blockToByteArray(block));
		 data.setContent(content);
		 try {
			keyChain_.sign(data, certificateName_);
		} catch (SecurityException e) {
			 throw new Error
	          ("SecurityException in sign: " + e.getMessage());
		} 
		System.out.println("***[Producer]*** : Sent sys.Block : [Previous sys.Block Hash] " + block.getPrevBlock());
		try {
			face.putData(data);
		} catch (IOException e) {
			 System.out.println("Producer: IOException in sending data " + e.getMessage());
		}
	}

	public void run() {
		try {
			face_ = new Face();
			keyChain_.setFace(face_);
			face_.setCommandSigningInfo(keyChain_, certificateName_);
		
			face_.registerPrefix(new Name(prefix), this, this);	
			// The main event loop.
			// Wait to receive one interest for the prefix.
			while(true){
				face_.processEvents();	//再等待处理接收到该名字的兴趣
				// We need to sleep for a few milliseconds so we don't use 100% of the CPU.
			//	Thread.sleep(10);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Producer: IOException in sending data " + e.getMessage());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			System.out.println("Producer: SecurityException in sending data " + e.getMessage());
		} catch (EncodingException e) {
				// TODO Auto-generated catch block
			System.out.println("Producer: EncodingException in sending data " + e.getMessage());
		} 
	/*	catch (InterruptedException e) {
					// TODO Auto-generated catch block
			System.out.println("Producer: InterruptedException in sending data " + e.getMessage());
		}
		*/
	}
}
