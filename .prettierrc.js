module.exports = {
    ...require('prettier-airbnb-config'),
    singleQuote: true,
    tabWidth: 4,
    'editor.tabSize': 4,
    printWidth: 120,
    'editor.formatOnSave': true,
    bracketSpacing: true,
    'react/jsx-filename-extension': [1, { extensions: ['.js', '.jsx', '.ts', '.tsx'] }],
};
