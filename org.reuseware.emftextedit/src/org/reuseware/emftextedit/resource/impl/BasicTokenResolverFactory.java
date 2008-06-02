package org.reuseware.emftextedit.resource.impl;

import org.reuseware.emftextedit.resource.TokenResolver;
import org.reuseware.emftextedit.resource.TokenResolverFactory;

import java.util.Map;
import java.util.HashMap;

public class BasicTokenResolverFactory implements TokenResolverFactory {

	private Map<String,TokenResolver> tokenName2TokenResolver;
	private static TokenResolver defaultResolver = new JavaBasedTokenResolver();
	
	public BasicTokenResolverFactory(){
		tokenName2TokenResolver = new HashMap<String,TokenResolver>();
	}
	
	public TokenResolver createTokenResolver(String tokenName) {		
		if(tokenName2TokenResolver.containsKey(tokenName)){
			return tokenName2TokenResolver.get(tokenName);
		}
		else{
			return defaultResolver;
		}
	}
	
	protected boolean registerTokenResolver(String tokenName, TokenResolver resolver){
		if(!tokenName2TokenResolver.containsKey(tokenName)){
			tokenName2TokenResolver.put(tokenName,resolver);
			return true;
		}
		return false;
	}
	
	protected TokenResolver deRegisterTokenResolver(String tokenName){
		return tokenName2TokenResolver.remove(tokenName);
	}

}
