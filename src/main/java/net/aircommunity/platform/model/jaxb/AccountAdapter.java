package net.aircommunity.platform.model.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.aircommunity.platform.model.domain.Account;

/**
 * Adapt a Account object to a string presentation
 * 
 * @author Bin.Zhang
 */
public class AccountAdapter extends XmlAdapter<String, Account> {

	@Override
	public String marshal(Account account) throws Exception {
		return account.getId();
	}

	@Override
	public Account unmarshal(String accountId) throws Exception {
		// we don't need unmarshal
		return null;
	}

}