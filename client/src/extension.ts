/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as path from 'path';
import { workspace, ExtensionContext } from 'vscode';

import {
	LanguageClient,
	LanguageClientOptions,
	ServerOptions
} from 'vscode-languageclient/node';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
	const root = 'serverStartScripts'
	const executable = process.platform === 'win32' ? 'de.unibonn.simpleml.ide.bat' : 'de.unibonn.simpleml.ide';
	const languageServerPath =  path.join(root, executable);
	const serverLauncher = context.asAbsolutePath(languageServerPath);
	const serverOptions: ServerOptions = {
		run: {
			command: serverLauncher,
			args: []
		},
		debug: {
			command: serverLauncher,
			args: ['-log', 'debug']
		}
	};

	// Options to control the language client
	let clientOptions: LanguageClientOptions = {
		// Register the server for plain text documents
		documentSelector: [{ scheme: 'file', language: 'simple-ml' }],
		synchronize: {
			// Notify the server about file changes to '.clientrc files contained in the workspace
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		},
		outputChannelName: 'Simple-ML Language Server'
	};

	// Create the language client and start the client.
	client = new LanguageClient(
		'simpleml',
		'Simple-ML',
		serverOptions,
		clientOptions
	);

	// Start the client. This will also launch the server
	client.start();
}

export function deactivate(): Thenable<void> | undefined {
	if (!client) {
		return undefined;
	}
	return client.stop();
}
