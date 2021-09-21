package br.com.aafl.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {
	

	private enum TipoComando {
		ZERAR, NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA, MAISMENOS, PORCENTAGEM;
	}
	
	private static final Memoria instancia = new Memoria();

	private final List<MemoriaObservador> observadores = new ArrayList<>();
	
	private String textoAtual = "";
	private String textoBuffer = ""; 
	private Boolean substituir = false;
	private TipoComando ultimaOperacao = null;
	
	private Memoria() {
	}
	
	public static Memoria getInstancia() {
		return instancia;
	}
	
	public void adicionarObservador(MemoriaObservador observador){
		observadores.add(observador);
	}
	
	public String getTextoAtual () {
		return textoAtual.isEmpty() ? "0" : textoAtual;
	}
	
	public void processarComando(String texto) {
		
		TipoComando tipoComando = detectarTipoComando(texto);
		
		
		if(tipoComando == null) { 
			return;
		} else if (tipoComando == TipoComando.ZERAR) {
			textoAtual = "";
			textoBuffer = "";
			substituir = false;
			ultimaOperacao = null;
		} else if (tipoComando == TipoComando.MAISMENOS) {
			if (Double.parseDouble(textoAtual.replace(",", ".")) >= 0) {
				textoBuffer = textoAtual;
				textoAtual = "-" + textoAtual;
			} else { 
				textoBuffer = textoAtual;
				textoAtual = textoAtual.replace("-", "");
			}
		} else if (tipoComando == TipoComando.VIRGULA) {
			if(getTextoAtual() == "0") {
				textoAtual = "0" + texto;
				substituir = false;
			} else
				textoAtual = substituir ? "0" + texto : textoAtual + texto;
				substituir = false;
		} else if (tipoComando == TipoComando.NUMERO 
					|| tipoComando == TipoComando.VIRGULA) {
			textoAtual = substituir ? texto : textoAtual + texto;
			substituir = false;
		} else { 
			substituir = true;
			textoAtual = obterResultadoOperacao();
			textoBuffer = textoAtual;
			ultimaOperacao = tipoComando;
			
		}
		
		observadores.forEach(o -> o.valorAlterado(getTextoAtual()));
	}

	private String obterResultadoOperacao() {
		if(ultimaOperacao == null) { 
			return textoAtual;
		}

		double numeroBuffer = Double.parseDouble(textoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(textoAtual.replace(",", "."));
		
		double resultado = 0;
		
		if(ultimaOperacao == TipoComando.SOMA) { 
			resultado = numeroBuffer + numeroAtual;
		} else if(ultimaOperacao == TipoComando.SUB) { 
			resultado = numeroBuffer - numeroAtual;
		} else if(ultimaOperacao == TipoComando.DIV) { 
			resultado = numeroBuffer / numeroAtual;
		} else if(ultimaOperacao == TipoComando.MULT) {
			resultado = numeroBuffer * numeroAtual;
		} else if (ultimaOperacao == TipoComando.IGUAL) {
			resultado = numeroAtual;
		} else if (ultimaOperacao == TipoComando.PORCENTAGEM) 
			resultado = numeroBuffer * 100 / numeroAtual;
		
		
		String resultadoString = Double.toString(resultado).replace(".", ",");
		
		boolean inteiro = resultadoString.endsWith(",0");
		
		return inteiro ? resultadoString.replace(",0", "") : resultadoString;
	
	}

	private TipoComando detectarTipoComando(String texto) {
		
		if(textoAtual.isEmpty() && texto == "0") { 
			return null;
		}
		
		try {
			Integer.parseInt(texto);
			return TipoComando.NUMERO;
		} catch (NumberFormatException e) {
			
			if ("AC".equals(texto)) { 
				return TipoComando.ZERAR;
			} else if ("/".equals(texto)) { 
				return TipoComando.DIV;
			} else if ("*".equals(texto)) { 
				return TipoComando.MULT;
			} else if ("+".equals(texto)) { 
				return TipoComando.SOMA;
			} else if ("-".equals(texto)) { 
				return TipoComando.SUB;
			} else if ("=".equals(texto)) { 
				return TipoComando.IGUAL;
			} else if (",".equals(texto) && !textoAtual.contains(",")) { 
				return TipoComando.VIRGULA;
			} else if ("±".equals(texto)) { 
				return TipoComando.MAISMENOS;
			} else if ("%".equals(texto)) { 
				return TipoComando.PORCENTAGEM;
			}
		}
	
		return null;
	}
	
}

