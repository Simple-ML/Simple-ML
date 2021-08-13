## Pull Requests

### Title

The title must follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) convention. **Mark breaking changes with an exclamation mark**.

All **types** from [Commitizen](https://github.com/commitizen/conventional-commit-types/blob/master/index.json) are allowed. Pick the one that fits best:

| Type | Meaning |
|------|---------|
| **`feat`** | New feature |
| **`fix`** | Bug fix |
| `docs` | Documentation only changes |
| `test` | Adding missing tests or correcting existing tests |
| `perf` | Changes that improve performance |
| `refactor` | Changes that neither fix a bug nor add a feature |
| `style` | Changes to code style (white-space, formatting, missing semi-colons, etc) |
| `build` | Changes that affect the build system or external dependencies (e.g. npm) |
| `ci` | Changes to CI configuration files and scripts (e.g. GitHub Actions) |
| `revert` | Reverting a previous commit |
| `chore` | Other changes that don't modify src or test files |

Possible **scopes** are:

| Scope | Meaning |
|------|---------|
| `devcontainer` | [VS Code development container](https://code.visualstudio.com/docs/remote/containers) |
| `data` | Integration of datasets or data libraries (e.g. pandas) |
| `dataCatalog` | List available datasets and their metadata |
| `ml` | Integration of ML libraries (e.g. scikit-learn) |
| `mlCatalog` | List available ML functions and their metadata |

Examples:

* `feat(devcontainer): add Python plugin`
* `docs: write contribution guide`
* `refactor!: drop support for Python 2` (the exclamation mark denotes a breaking change)

### Description

Use the [provided template](./pull_request_template.md). It should be suggested automatically.
