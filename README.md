# 🎼 SC-VexFlow Staves

Esta extensão permite a renderização de notação musical profissional dentro do SuperCollider utilizando a biblioteca **VexFlow 5**. A integração é feita através de um `WebView` que injeta dinamicamente o motor gráfico JavaScript em um template HTML.

## 🚀 Funcionalidades Implementadas

* 💉 **Injeção Dinâmica de Código**: A classe lê o arquivo `vexflow.js` local e o injeta diretamente no HTML para contornar restrições de segurança de arquivos locais (CORS).
* 🎨 **Renderização SVG**: Utiliza o backend SVG do VexFlow para garantir máxima qualidade visual e redimensionamento sem perda de resolução.
* 🎼 **Suporte a Claves**: Implementação de Claves de Sol (`treble`), Fá (`bass`) e suporte nativo para Claves de Dó (Alto, Tenor, etc.).
* ⏱️ **Fórmulas de Compasso**: Suporte para definições dinâmicas de compasso (ex: "4/4", "7/8", "3/2").
* 🔄 **Sincronização Assíncrona**: Gerenciamento de promessas do VexFlow 5 para garantir que as fontes (`Bravura`/`Academico`) sejam carregadas antes do desenho.
* 🎹 **Grand Staff Automático**: Distribuição inteligente de notas entre pautas de Sol e Fá baseada na altura (split point no Dó Central).
* 📏 **Escalonamento Dinâmico**: Controle de tamanho da notação (`small`, `medium`, `large`) configurável via SuperCollider.
* ✨ **Microtonalidade**: Suporte nativo para quartos de tom (MIDI `x.5`) com acidentes dedicados (Stein-Zimmermann).
* 3️⃣ **Tupletas Avançadas**: Detecção automática de quiálteras (tercinas, quintinas, etc.) com cálculo de proporção e formatação inteligente de brackets.

## 📁 Estrutura de Arquivos

Para o funcionamento correto, os arquivos devem estar na mesma pasta:
- 📄 `Staves.sc`: Definição da classe e lógica de ponte.
- 🌐 `Staves.html`: Template com a função `window.drawMusic`.
- ⚙️ `vexflow.js`: A biblioteca VexFlow (Versão 5.x).

## 🛠️ Como usar

```supercollider
// Inicializar a classe
a = Staves.new;

// 1. Configurar a visualização
// Argumentos: timeSig, keySig, size ("small", "medium", "large")
a.setup("3/4", "C", "large");

// 2. Gerar partitura a partir de um Pbind
// O sistema converte automaticamente durações e alturas (incluindo microtons)
p = Pbind(
    \degree, Pseq((0..5), 1),
    \dur, Pseq([
        0.5/5, 0.5/5, 0.5/5, 0.5/5, 0.5/5, // Quintina
        0.25, 0.25,                        // Semicolcheias
        0.5                                // Colcheia
    ], 1)
);

// Renderiza 8 eventos do Pbind
a.createScore(p, 8);
```
## 📚 Referências Técnicas

A integração utiliza a **API do VexFlow v5**. Para consultar métodos, propriedades e argumentos, utilize o link oficial:

🔗 [VexFlow API Reference - Official](https://0xfe.github.io/vexflow/api/)

### 🔑 Glossário de Classes (Namespace `VF`)

Para facilitar a leitura da documentação oficial, utilize esta tabela de equivalência entre os nomes da API e a implementação no seu código:

| Classe na API | Uso no seu Código | Função |
| :--- | :--- | :--- |
| `Vex.Flow.Renderer` | `VF.Renderer` | Gerencia a criação do elemento gráfico (SVG/Canvas). |
| `Vex.Flow.Stave` | `VF.Stave` | Define a pauta (posição, linhas, claves e compassos). |
| `Vex.Flow.StaveNote` | `VF.StaveNote` | Cria as cabeças das notas, hastes e acidentes. |
| `Vex.Flow.Voice` | `VF.Voice` | Organiza as notas para que a soma rítmica bata com o compasso. |
| `Vex.Flow.Formatter` | `VF.Formatter` | Calcula o espaçamento horizontal (justificação) das notas. |

### 📑 Principais Métodos Implementados

* ✅ **`VF.Stave.addClef(clef)`**: Define a clave. Aceita strings como `"treble"`, `"bass"`, `"alto"`, `"tenor"`.
* ✅ **`VF.Stave.addTimeSignature(time)`**: Define a fórmula de compasso (ex: `"4/4"`, `"6/8"`).
* ✅ **`VF.Renderer.getContext()`**: Retorna o objeto `context`, responsável por executar as operações de desenho.
* ✅ **`VF.loadFonts(...)`**: Método assíncrono (Promise) obrigatório na v5 para carregar glifos musicais (`Bravura`/`Academico`).

---
*⌨️ Documentação gerada para integração SuperCollider + VexFlow.*


graph TD
    A[SuperCollider: Pbind/Event] -->|asJSON| B(String JSON)
    B -->|webView.runJavaScript| C{Browser / WebView}
    
    subgraph JavaScript Pipeline
    C --> D[JSON.parse: Recupera Array de Notas]
    D --> E[Loop forEach: Processamento Individual]
    
    E --> F1[Conversão MIDI -> Nome da Nota/Oitava]
    E --> F2[Conversão Dur -> String VexFlow]
    E --> F3[Identificação de Acidentes]
    
    F1 & F2 & F3 --> G[Instanciar VF.StaveNote]
    
    G --> H{Filtro de Registro}
    H -->|> 60| I[Array upperNotes]
    H -->|< 60| J[Array lowerNotes]
    
    I & J --> K[VF.Voice: Organização Rítmica]
    K --> L[VF.Formatter: Justificação e Espaçamento]
    L --> M[VF.Beam: Geração de Barras Automáticas]
    end
    
    M --> N((Renderização SVG Final))
