import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Tutoriais usados:
/*
 --> https://www.devmedia.com.br/criando-uma-conexao-java-mysql-server/16753
 */

public class BancoDeDados {

    public static String status = "Não conectou...";

    public BancoDeDados() {
    }
    public static java.sql.Connection getConexaoMySQL() {

        Connection connection = null;
        try {
            System.out.println("Iniciando conexão!");
            String driverName = "com.mysql.cj.jdbc.Driver";
            Class.forName(driverName);
            String serverName = "localhost:3306";    //caminho do servidor do BD
            String mydatabase ="projetobanco";        //nome do seu banco de dados
            String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
            String username = "root";        //nome de um usuário de seu BD
            String password = "tina&tadel";      //sua senha de acesso
            connection = DriverManager.getConnection(url, username, password);

            //Testa sua conexão//
            if (connection != null) {
                status = ("STATUS--->Conectado com sucesso!");
            } else {
                status = ("STATUS--->Não foi possivel realizar conexão");
            }
            return connection;
        } catch (ClassNotFoundException e) {  //Driver não encontrado
            System.out.println("O driver expecificado nao foi encontrado." + e);
            return null;
        } catch (SQLException e) {
            //Não conseguindo se conectar ao banco
            System.out.println("Nao foi possivel conectar ao Banco de Dados." + e);
            e.printStackTrace();
            return null;
        }
    }

    //Método que retorna o status da sua conexão//
    public static String statusConection() {
        return status;
    }
    //Método que fecha sua conexão//
    public static boolean FecharConexao() {
        try {
            BancoDeDados.getConexaoMySQL().close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    //Método que reinicia sua conexão//
    public static java.sql.Connection ReiniciarConexao() {
        FecharConexao();
        return BancoDeDados.getConexaoMySQL();
    }

    public static boolean salvaBanco(Banco banco) {
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql = "INSERT INTO banco (nome) VALUES(\'"+ banco.getNome() +"\')";
            // Execute the insert statement
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public static  List<Banco> getBancos(){
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql = "SELECT * FROM Banco";
            // Execute the insert statement
            ResultSet rs = stmt.executeQuery(sql);
            List<Banco> bancos=new ArrayList<Banco>();
            while(rs.next()) {
                Banco banco = new Banco(Integer.parseInt(rs.getString("idBanco")), rs.getString("nome"));
                bancos.add(banco);
            }

            return bancos;
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }


    public static  List<Historico> getHistorico(Integer contaId){
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql = "SELECT * FROM Historico WHERE conta = " + contaId +"";
            // Execute the insert statement
            ResultSet rs = stmt.executeQuery(sql);
            List<Historico> historicos = new ArrayList<Historico>();
            while(rs.next()) {
                Historico historico = new Historico(rs.getString("operacao"),
                                                    rs.getString("data"),
                                                    Double.parseDouble(rs.getString("valor")));
                historicos.add(historico);
            }

            return historicos;
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }


    public static boolean salvaConta(ContaGeral conta, Integer banco) {
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            String sql = "INSERT INTO " +
                    "conta (numero, agencia,  cliente, tipo, ativa,  saldo, limite, banco) " +
                    "VALUES(\'"+
                    conta.getNumero()   + "\',\'" +
                    conta.getAgencia()  + "\',\'"+
                    conta.getCliente()  + "\',\'"+
                    conta.getTipo()     + "\',"+
                    conta.getAtiva()    + ","+
                    conta.getSaldo()    + ","+
                    conta.getLimit()    + ","+
                    banco + ")";
            // Execute the insert statement
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public static  List<ContaGeral> getContas(){
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql = "SELECT * FROM Conta";
            // Execute the insert statement
            ResultSet rs = stmt.executeQuery(sql);
            List<ContaGeral> contas=new ArrayList<>();
            while(rs.next()) {
                ContaGeral conta = null;
                if(rs.getString(  "tipo").equals("CONTA CORRENTE")){
                    conta = new ContaCorrente();
                }else if(rs.getString(  "tipo").equals("CONTA ESPECIAL")){
                    conta = new ContaEspecial();
                }else{
                    conta = new ContaPoupanca();
                }
                conta.setId(Integer.parseInt(rs.getString( "id")));
                conta.setNumero(rs.getString(  "cliente"));
                conta.setAgencia(rs.getString( "agencia"));
                conta.setAtiva(Integer.parseInt(rs.getString( "ativa")));
                conta.setSaldo(Double.parseDouble(rs.getString( "saldo")));
                conta.setTipo(rs.getString(  "tipo"));
                conta.setLimit(Double.parseDouble(rs.getString(  "limite")));
                contas.add(conta);
            }

            return contas;
        } catch (SQLException e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public static boolean atualizarSaldo(Integer idConta, Double saldo){

        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql ="UPDATE conta SET saldo="+saldo+" WHERE idConta = "+ idConta +";";
            // Execute the insert statement
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

    }

    public static  ContaGeral getUmaConta(Integer codigoBanco, String numero, String agencia){
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql = "SELECT * FROM conta WHERE banco = " + codigoBanco + " && numero LIKE \'"+ numero +"\' && agencia LIKE \'"+ agencia +"\';";
            // Execute the insert statement
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            ContaGeral conta = null;
            if(rs.getString(  "tipo").equals("CONTA CORRENTE")){
                conta = new ContaCorrente();
            }else if(rs.getString(  "tipo").equals("CONTA ESPECIAL")){
                conta = new ContaEspecial();
            }else{
                conta = new ContaPoupanca();
            }
            conta.setId(Integer.parseInt(rs.getString( "idConta")));
            conta.setNumero(rs.getString(  "numero"));
            conta.setCliente(rs.getString(  "cliente"));
            conta.setAgencia(rs.getString( "agencia"));
            conta.setAtiva(Integer.parseInt(rs.getString( "ativa")));
            conta.setSaldo(Double.parseDouble(rs.getString( "saldo")));
            conta.setTipo(rs.getString(  "tipo"));
            conta.setLimit(Double.parseDouble(rs.getString(  "limite")));

            return conta;

        } catch (SQLException e) {
            System.out.println(e);
            return null;
        }
    }

    public static boolean atualizarStatus(Integer idConta, Integer status){

        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            // Prepare a statement to insert a record
            String sql ="UPDATE conta SET ativa="+ status +" WHERE idConta = "+ idConta +";";
            // Execute the insert statement
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }

    }

    public static boolean salvaHistorico(Historico historico) {
        try {
            Statement stmt = BancoDeDados.getConexaoMySQL().createStatement();

            String sql = "INSERT INTO " +
                    "historico (operacao, data, valor,  conta) " +
                    "VALUES(\'"+
                    historico.getTipoMoveimento()   + "\',\'" +
                    historico.getData()  + "\',"+
                    historico.getValor() + ","+
                    historico.getConta() + ")";
            // Execute the insert statement
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }


}