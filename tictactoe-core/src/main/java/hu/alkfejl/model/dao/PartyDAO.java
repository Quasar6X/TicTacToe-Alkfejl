package hu.alkfejl.model.dao;

import hu.alkfejl.model.Party;
import org.apache.log4j.Logger;

public interface PartyDAO extends DAO<Party> {
    Logger LOG = Logger.getLogger(PartyDAO.class);
}
