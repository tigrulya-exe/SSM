const eslintCheck = (filenames) => `eslint ${filenames.join(' ')} --config ./.eslintrc.full.json --ext ts,tsx --report-unused-disable-directives --max-warnings 0`;

export default {
  '*.(js|jsx|ts|tsx)': (filenames) =>
    // Run ESLint on entire repo if more than 10 staged files
    filenames.length > 10 ? 'lint' : eslintCheck(filenames),
}