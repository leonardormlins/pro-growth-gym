package hello;

import java.util.Random;
import java.util.List;
import java.util.LinkedList;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Query;


public class Model {

	
	ObjectContainer alunos = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/alunos.db4o");
	ObjectContainer aulas = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/aulas.db4o");
	ObjectContainer adms = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/adms.db4o");
	ObjectContainer professores = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/professores.db4o");
	ObjectContainer funcionarios = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/funcionarios.db4o");
	ObjectContainer acessos = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/acessos.db4o");
	ObjectContainer shapes = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/shapes.db4o");
	ObjectContainer modalidades = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/modalidades.db4o");
	ObjectContainer codigosAcesso = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "bd/codigosAcesso.db4o");
	
	
	//adiciona nova aula no bd
	public void addAula (Aula aula) {
			aulas.store(aula);
			aulas.commit();
		}
	 
	
	//inclui aula ao aluno
	public void inclAula (String nAluno, String nAula) {
		Query query1 = alunos.query();
		query1.constrain(Aluno.class);
		
		List<Aluno> todosAlunos = query1.execute();
		
		Query query2 = aulas.query();
		query2.constrain(Aula.class);
		
		List<Aula> todasAulas = query2.execute();
		
		for (Aluno aluno: todosAlunos) {
			if (aluno.getNome().equals(nAluno))
			{
				for (Aula aula: todasAulas) {
					if (aula.getDescricao().equals(nAula)){
						aluno.addAula(aula);
					}
						
					}
				}
		}
	}

	//verifica se o e-mail está disponível para cadastro do aluno
	public boolean isAlunoDisponivel(String email){
		Query query = alunos.query();
		query.constrain(Aluno.class);
	    List<Aluno> todosAlunos = query.execute();
	    
	    for(Aluno aluno: todosAlunos){
	    	if(aluno.getEmail().equals(email)) return false;
	    }
	    
	    return true;
	}


	//cadastra aluno
	public boolean addAluno (Aluno aluno) {
		if (isAlunoDisponivel(aluno.getEmail())) {
			//verificar as aulas que já estão inseridas
			aluno.addAula(new Aula (null, "Zumba", "segunda", "50min", new Modalidade (01,"zumba")));
			aluno.addAula(new Aula (null, "Boxe", "terÃ§a", "50min",  new Modalidade (02,"boxe")));
			aluno.addAula(new Aula (null, "Spinning", "Quarta", "50min", new Modalidade (03,"spinning")));
			alunos.store(aluno);
			alunos.commit();
			return true;
		}
		return false;
	}

	//retorna as aulas por aluno
	public List<Aula> buscarAulasAluno(String email){
		Query query = alunos.query();
		query.constrain(Aluno.class);
		
		List<Aluno> todosAlunos = query.execute();
		
		for (Aluno aluno:todosAlunos) {
			if (aluno.getEmail().equals(email))
				return aluno.getAula();
		}
		return null;
	}
	
	
	//login
	public Aluno loginUser(String email, String senha)
	{
		Query query1 = alunos.query();
		query1.constrain(Aluno.class);
		
		List<Aluno> todosAlunos = query1.execute();
		for(Aluno pessoa:todosAlunos) {
			if (pessoa.getEmail().equals(email)&&pessoa.getSenha().equals(senha))return pessoa;
		}
		return null;
	}
	
	//cadastro de aluno
	public void cadastrarAluno(String nome, String senha, String genero, String email, String ender, String tel,String cpf) {

		addAluno(new Aluno (Integer.valueOf(cpf),nome,ender,email,tel, senha, genero.charAt(0),1, null, null, 
    			new LinkedList<Aula>()));
	}

	
	//retorna todos os alunos cadastrados
	public List<Aluno> mostAlunos () {
		Query query1 = alunos.query();
		query1.constrain(Aluno.class);
		List<Aluno> todosAlunos = query1.execute();
		return todosAlunos;
		
	}
	
	//busca aluno por cpf
	public Aluno buscaAluno(int cpf) {
		Query query = alunos.query();
		query.constrain(Aluno.class);
		List<Aluno> todosAlunos = query.execute();
		for (Aluno aluno: todosAlunos) {
			if (aluno.getId()==cpf) return aluno;
		}
		return null;
	}
	
	//altera infos do aluno
	public void alteraAluno(String nome, String senha, String genero, String email, String ender, String tel, String cpf) {
		Aluno aluno = buscaAluno(Integer.valueOf(cpf));
		if (!(aluno.getNome().equals(nome))) aluno.setNome(nome);
		if (!(aluno.getSenha().equals(senha))) aluno.setSenha(senha);
		if (!(aluno.getGenero()==genero.charAt(0))) aluno.setGenero(genero.charAt(0));
		if (!(aluno.getEmail().equals(email))) aluno.setEmail(email);
		if (!(aluno.getAdress().equals(ender))) aluno.setAdress(ender);
		if (!(aluno.getTelefone().equals(tel))) aluno.setTelefone(tel);
		alunos.store(aluno);
		alunos.commit();
	}
	
	//desmatricula aulas de aluno
	public Aula desmatAulaAluno(String email, String nAula){
		Query query = alunos.query();
		query.constrain(Aluno.class);
		
		List<Aluno> todosAlunos = query.execute();
		
		for (Aluno aluno:todosAlunos) {
			if (aluno.getEmail().equals(email)) {
				for (Aula aula: aluno.getAula()) {
					if (aluno.getAula().indexOf(aula)==Integer.valueOf(nAula)){
						Aula aulaRem=aula;
						aluno.getAula().remove(aula);
						alunos.store(aluno);
						alunos.commit();
						return aulaRem;
					}
				}
			}
		}
		return null;
	}
	
	//Cria um codigo valido para cadastro e insere no banco
	public void novoCodigo(){
		int valorParaInserir = gerarValorCode();
		codigosAcesso.store(valorParaInserir);
		codigosAcesso.commit();
	}
	
	//Gera um codigo
	public int gerarValorCode(){
		Random gerador = new Random();
		int aux = gerador.nextInt(10000);
		return aux;	
	}
	
	//Remove codigo
}
