declare module 'monaco-editor/esm/vs/editor/common/services/languageFeatures.js' {
  export const ILanguageFeaturesService: { documentSymbolProvider: unknown };
}

declare module 'monaco-editor/esm/vs/editor/common/modelLineProjectionData.js' {
  export class InjectedText {
    constructor(public readonly options: InjectedTextOptions) {}
  }
}

declare module 'monaco-editor/esm/vs/editor/contrib/documentSymbols/browser/outlineModel.js' {
  import { type editor, type languages } from 'monaco-editor';

  interface TreeElement {
    id: string;
    parent?: TreeElement;
    symbol: languages.DocumentSymbol;
    children: Map<number, TreeElement>;
  }

  export abstract class OutlineModel {
    static create(registry: unknown, model: editor.ITextModel): Promise<OutlineModel>;

    children: Map<number, TreeElement>;
    asListOfDocumentSymbols(): languages.DocumentSymbol[];
  }
}

declare module 'monaco-editor/esm/vs/editor/standalone/browser/standaloneServices.js' {
  export const StandaloneServices: {
    get: (id: unknown) => { documentSymbolProvider: unknown };
  };
}
