import { defineConfig } from 'vite';
import tsConfigPaths from 'vite-tsconfig-paths';
import createSvgSpritePlugin from 'vite-plugin-svg-spriter'
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    tsConfigPaths(),
    createSvgSpritePlugin({ svgFolder: './src/components/uikit/Icon/icons' }),
    react(),
  ],
  resolve: {
    extensions: ['.tsx', '.ts', '.json', '.mts', '.mjs', '.js', '.jsx'],
  },
});
