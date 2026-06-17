import os

# 1. Walk and replace contents
for root, dirs, files in os.walk('.'):
    # Skip build and IDE folders
    if any(p in root for p in ['.git', '.idea', 'target', '.mvn']):
        continue
    for file in files:
        ext = os.path.splitext(file)[1]
        if ext in ['.java', '.xml', '.properties', '.bat', '.fxml', '.md', '.txt']:
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
                if 'dean11' in content:
                    content = content.replace('dean11', 'dean12')
                    with open(filepath, 'w', encoding='utf-8') as f:
                        f.write(content)
                    print(f"Replaced in: {filepath}")
            except Exception as e:
                try:
                    with open(filepath, 'r', encoding='latin-1') as f:
                        content = f.read()
                    if 'dean11' in content:
                        content = content.replace('dean11', 'dean12')
                        with open(filepath, 'w', encoding='latin-1') as f:
                            f.write(content)
                        print(f"Replaced (latin-1) in: {filepath}")
                except Exception as ex:
                    print(f"Failed to process {filepath}: {ex}")

# 2. Rename directories
java_old = os.path.join('src', 'main', 'java', 'com', 'example', 'dean11')
java_new = os.path.join('src', 'main', 'java', 'com', 'example', 'dean12')
if os.path.exists(java_old):
    os.rename(java_old, java_new)
    print("Renamed java directory to dean12")

resources_old = os.path.join('src', 'main', 'resources', 'com', 'example', 'dean11')
resources_new = os.path.join('src', 'main', 'resources', 'com', 'example', 'dean12')
if os.path.exists(resources_old):
    os.rename(resources_old, resources_new)
    print("Renamed resources directory to dean12")
