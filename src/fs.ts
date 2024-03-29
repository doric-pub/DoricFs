import { BridgeContext } from "doric";

export type PathLike = string;

export type Stats = {};

export function fs(context: BridgeContext) {
  return {
    getDocumentsDir: (options?: { external?: boolean }) => {
      return context.callNative(
        "fs",
        "getDocumentsDir",
        options || {}
      ) as Promise<PathLike>;
    },

    exists: (path: PathLike) => {
      return context.callNative("fs", "exists", path) as Promise<boolean>;
    },

    stat: (path: PathLike) => {
      return context.callNative("fs", "stat", path) as Promise<Stats>;
    },

    isFile: (path: PathLike) => {
      return context.callNative("fs", "isFile", path) as Promise<boolean>;
    },

    isDirectory: (path: PathLike) => {
      return context.callNative("fs", "isDirectory", path) as Promise<boolean>;
    },

    mkdir: (path: PathLike) => {
      return context.callNative("fs", "mkdir", path) as Promise<boolean>;
    },

    readDir: (path: PathLike) => {
      return context.callNative("fs", "readDir", path) as Promise<string[]>;
    },

    readFile: (path: PathLike) => {
      return context.callNative("fs", "readFile", path) as Promise<string>;
    },

    readBinaryFile: (path: PathLike) => {
      return context.callNative("fs", "readFile", path) as Promise<ArrayBuffer>;
    },

    writeFile: (path: PathLike, content: string | ArrayBuffer) => {
      return context.callNative("fs", "writeFile", {
        path,
        content,
      }) as Promise<void>;
    },

    appendFile: (path: PathLike, content: string | ArrayBuffer) => {
      return context.callNative("fs", "appendFile", {
        path,
        content,
      }) as Promise<void>;
    },

    delete: (path: PathLike) => {
      return context.callNative("fs", "delete", path) as Promise<boolean>;
    },

    rename: (src: PathLike, dest: PathLike) => {
      return context.callNative("fs", "rename", {
        src,
        dest,
      }) as Promise<boolean>;
    },

    copy: (src: PathLike, dest: PathLike) => {
      return context.callNative("fs", "copy", {
        src,
        dest,
      }) as Promise<boolean>;
    },

    choose: (options: {
      uniformTypeIdentifiers: string[];
      mimeType: string;
    }) => {
      return context.callNative("fs", "choose", options) as Promise<string>;
    },
  };
}
