package br.com.aafl.cm.visao;

import java.awt.GridLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import br.com.aafl.cm.modelo.Tabuleiro;


@SuppressWarnings("serial")
public class PainelTabuleiro extends JPanel {

	public PainelTabuleiro(Tabuleiro tabuleiro) {
		
		setLayout(new GridLayout(
				tabuleiro.getLinhas(), tabuleiro.getColunas())); 

	
		tabuleiro.paraCada(c -> add(new BotaoCampo(c)));
	
		
		tabuleiro.registrarObservador(e -> {
			
			SwingUtilities.invokeLater(() -> { 
				if(e.isGanhou()) { 
					JOptionPane.showMessageDialog(this, "Você venceu!");
				} else {
					JOptionPane.showMessageDialog(this, "Perdeu!");
				}
				
				tabuleiro.reiniciar();
			});
		});
		
	}
}
