'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

function fs(context) {
    return {
        getDocumentsDir: (options) => {
            return context.callNative("fs", "getDocumentsDir", options || {});
        },
        exists: (path) => {
            return context.callNative("fs", "exists", path);
        },
        stat: (path) => {
            return context.callNative("fs", "stat", path);
        },
        isFile: (path) => {
            return context.callNative("fs", "isFile", path);
        },
        isDirectory: (path) => {
            return context.callNative("fs", "isDirectory", path);
        },
        mkdir: (path) => {
            return context.callNative("fs", "mkdir", path);
        },
        readDir: (path) => {
            return context.callNative("fs", "readDir", path);
        },
        readFile: (path) => {
            return context.callNative("fs", "readFile", path);
        },
        writeFile: (path, content) => {
            return context.callNative("fs", "writeFile", {
                path,
                content,
            });
        },
        appendFile: (path, content) => {
            return context.callNative("fs", "appendFile", {
                path,
                content,
            });
        },
        delete: (path) => {
            return context.callNative("fs", "delete", path);
        },
        rename: (src, dest) => {
            return context.callNative("fs", "rename", {
                src,
                dest,
            });
        },
        copy: (src, dest) => {
            return context.callNative("fs", "copy", {
                src,
                dest,
            });
        },
        choose: (options) => {
            return context.callNative("fs", "choose", options);
        },
    };
}

exports.fs = fs;
//# sourceMappingURL=bundle_fs.js.map
