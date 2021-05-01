package hu.alkfejl.model.dao;

import hu.alkfejl.model.Move;
import org.apache.log4j.Logger;

public interface MoveDAO extends DAO<Move> {
    Logger LOG = Logger.getLogger(MoveDAO.class);
}
