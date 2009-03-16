package org.kuali.kfs.module.cab.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;

//FIXME--to be removed later when CAB testing is over
/**
 * This class is created for temporarily incrementing the sequence so that old data can be res-stored
 */
public class SequenceIncrementerStep extends AbstractStep {
    protected Connection getConnection() throws SQLException {
        DataSource dataSource = SpringContext.getBean(DataSource.class);
        Connection connection = dataSource.getConnection();
        return connection;
    }

    public void incrementAll() {
        try {
            Connection connection = getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select sequence_name from user_sequences order by 1");
            while (rs != null && rs.next()) {
                String seqName = rs.getString(1);
                // Alter the increment
                String alterSql = "ALTER SEQUENCE " + seqName + " INCREMENT BY 1";
                executeStatement(connection, alterSql);

                // Increment the sequence, resulting a 2001 increment
                String updateSql = "select " + seqName + ".Nextval from dual";
                executeStatement(connection, updateSql);

                // Reset the increment
                String reverse = "ALTER SEQUENCE " + seqName + " INCREMENT BY 1";
                executeStatement(connection, reverse);
                System.out.println(seqName);
            }
            rs.close();
            stmt.close();
            connection.close();
        }
        catch (Exception e) {
            // DO NOTHING
            // e.printStackTrace();
        }
    }

    private void executeStatement(Connection connection, String alterSql) throws SQLException {
        PreparedStatement alterStmt = connection.prepareStatement(alterSql);
        alterStmt.execute();
        alterStmt.close();
    }

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        incrementAll();
        return true;
    }

}
