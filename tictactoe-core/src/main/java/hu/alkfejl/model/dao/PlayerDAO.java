package hu.alkfejl.model.dao;

import hu.alkfejl.model.Player;
import org.apache.log4j.Logger;

public interface PlayerDAO extends DAO<Player> {
    Logger LOG = Logger.getLogger(PlayerDAO.class);
}
