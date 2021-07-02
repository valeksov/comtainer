module.exports = {
    root: true,
    extends: ['airbnb', 'airbnb/hooks'],
    rules: {
        indent: ['error', 4],
        quotes: ['error', 'single'],
        semi: ['error', 'always'],
        'comma-dangle': 0,
        'import/no-unresolved': 'off',
        'no-unused-vars': 'off',
        'dot-notation': 'off',
        'object-curly-newline': 'off',
        'import/prefer-default-export': 'off',
        'prefer-template': 'off',
        'arrow-body-style': 'off',
        'no-restricted-syntax': 'off',
        'operator-linebreak': 'off',
        'react-hooks/exhaustive-deps': 'off',
        'prefer-destructuring': ['error', { object: false, array: false }],
        'import/extensions': 0,
        'react/jsx-indent': ['error', 4],
        'max-len': ['error', { code: 120 }],
        'linebreak-style': ['error', 'windows'],
        'arrow-parens': ['error', 'as-needed'],
        'react/jsx-filename-extension': [1, { extensions: ['.js', '.jsx', '.ts', '.tsx'] }],
    },
    settings: {
        'import/resolver': {
            node: {
                extensions: ['.js', '.jsx', '.ts', '.tsx'],
            },
        },
    },
};
