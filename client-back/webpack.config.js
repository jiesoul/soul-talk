const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const path = require("path");

module.exports = {
    entry: [
        "./src-js/theme.js",
        "./resources/public/js/main.js"
    ],
    output: {
        path:path.resolve(__dirname, "resources/public/js"),
        filename: "index_bundle.js"
    },
    plugins: [
        new MiniCssExtractPlugin({
            filename: "[name].css",
            chunkFilename: "[id].css"
        })
    ],
    module: {
        rules: [
            {
                test: /\.less$/,
                use: [
                    {
                        loader: 'style-loader'
                    },
                    {
                        loader: 'css-loader'
                    },
                    {
                        loader: 'less-loader',
                        options: {
                            sourceMap: true,
                            lessOptions: {
                                modifyVars: {
                                    'primary-color': '#1DA57A',
                                    'link-color': '#1DA571',
                                    'border-radius-bae': '2px',
                                    'hack': 'true; @import "default.less";'
                                },
                                javascriptEnabled: true,
                            }
                        }
                    }
                ]
            },
            {
                test: /\.js$/,
                exclude: /node_modules/,
                use: "babel-loader"
            }, {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: "babel-loader"
            }
        ]
    }
};

