package br.com.projeto.estudioBDD;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import br.com.projeto.estudioBDD.database.model.Cenario;
import br.com.projeto.estudioBDD.database.model.enums.DecisaoSugestao;
import br.com.projeto.estudioBDD.database.model.HistoriaUsuario;
import br.com.projeto.estudioBDD.database.model.Sugestao;
import br.com.projeto.estudioBDD.database.model.enums.TipoPasso;
import br.com.projeto.estudioBDD.database.model.Usuario;
import br.com.projeto.estudioBDD.database.repository.CenarioRepo;
import br.com.projeto.estudioBDD.database.repository.SugestaoRepo;
import br.com.projeto.estudioBDD.dto.CenarioDto;
import br.com.projeto.estudioBDD.dto.SugestaoDto;
import br.com.projeto.estudioBDD.exception.RegraNegocioException;
import br.com.projeto.estudioBDD.service.CenarioService;
import br.com.projeto.estudioBDD.service.ExportacaoService;
import br.com.projeto.estudioBDD.service.HistoriaService;
import br.com.projeto.estudioBDD.service.SugestaoService;
import br.com.projeto.estudioBDD.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class RegraDeNegocioTest {

        @Mock
        CenarioRepo cenarioRepository;
        @Mock
        SugestaoRepo sugestaoRepository;
        @Mock
        HistoriaService historiaService;
        @Mock
        UsuarioService usuarioService;

        @InjectMocks
        CenarioService cenarioService;
        @InjectMocks
        SugestaoService sugestaoService;
        @InjectMocks
        ExportacaoService exportacaoService;

        // Regra 2: cenário válido precisa de ao menos um passo Então
        @Test
        void cenarioSemPassoEntaoERejeitado() {
                HistoriaUsuario historia = HistoriaUsuario.builder().id(1L).build();
                when(historiaService.obterParaEdicao(1L, 1L)).thenReturn(historia);

                CenarioDto.Request req = new CenarioDto.Request("Sem então", null, List.of(
                                new CenarioDto.PassoRequest(TipoPasso.DADO, "que estou na tela de login"),
                                new CenarioDto.PassoRequest(TipoPasso.QUANDO, "informo e-mail e senha")));

                RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                                () -> cenarioService.criar(1L, 1L, req));

                assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ex.getStatus());
                assertTrue(ex.getMessage().contains("Então"));
                verify(cenarioRepository, never()).save(any());
        }

        // Regra 4: a rejeição de uma sugestão exige um motivo
        @Test
        void rejeicaoDeSugestaoSemMotivoERecusada() {
                Sugestao pendente = Sugestao.builder().id(1L).decisao(DecisaoSugestao.PENDENTE).build();
                when(sugestaoRepository.findById(1L)).thenReturn(Optional.of(pendente));
                when(usuarioService.obterEntidade(1L)).thenReturn(Usuario.builder().id(1L).build());

                SugestaoDto.DecisaoRequest req = new SugestaoDto.DecisaoRequest(DecisaoSugestao.REJEITADA, null,
                                "sem motivo informado");

                RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                                () -> sugestaoService.decidir(1L, 1L, req));

                assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ex.getStatus());
                assertEquals(DecisaoSugestao.PENDENTE, pendente.getDecisao()); // continua pendente
                verify(sugestaoRepository, never()).save(any());        
        }

        // Regra 5: não exporta história com sugestão pendente
        @Test
        void exportacaoEBloqueadaComSugestaoPendente() {
                HistoriaUsuario historia = HistoriaUsuario.builder()
                                .id(1L)
                                .titulo("Login de cliente")
                                .cenarios(List.of(Cenario.builder().titulo("Login com sucesso").build()))
                                .build();
                when(historiaService.obterEntidade(1L)).thenReturn(historia);
                when(sugestaoRepository.existsByHistoriaUsuarioIdAndDecisao(1L, DecisaoSugestao.PENDENTE))
                                .thenReturn(true);

                RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                                () -> exportacaoService.exportar(1L));

                assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ex.getStatus());
                assertTrue(ex.getMessage().contains("pendentes"));
        }
}