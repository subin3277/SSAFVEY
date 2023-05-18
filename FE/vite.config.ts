import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import { VitePWA } from 'vite-plugin-pwa';
import path from 'path';
import comporession from 'vite-plugin-compression2';

export default defineConfig({
  // esbuild: {
  //   // drop: ['console', 'debugger'],
  //   supported: {
  //     'top-level-await': true,
  //   },
  // },
  plugins: [
    react(),
    VitePWA(),
    comporession({
      include: [/\.(js)$/, /\.(css)$/],
      threshold: 1000,
    }),
  ],
  resolve: {
    alias: [
      { find: '@', replacement: path.resolve(__dirname, './src') },
      { find: '@components', replacement: path.resolve(__dirname, './src/Components') },
      { find: '@store', replacement: path.resolve(__dirname, './src/Store') },
      { find: '@api', replacement: path.resolve(__dirname, './src/Api') },
      { find: '@util', replacement: path.resolve(__dirname, './src/Util') },
    ],
  },
  server: {
    host: '0.0.0.0',
  },
});
