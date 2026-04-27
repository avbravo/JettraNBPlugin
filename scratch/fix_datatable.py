import sys

file_path = '/home/avbravo/NetBeansProjects/jettrastack_local/JettraWorkspace/JettraNBPlugin/src/main/resources/io/jettra/nb/index.html'
with open(file_path, 'r') as f:
    lines = f.readlines()

target = "const args = headerRowMatch[2].split(',').map(s => s.trim().replace(/\"/g, ''));"
replacement = """const argsRaw = headerRowMatch[2];
                                      const args = parseBalancedArgs(argsRaw).map(a => {
                                          const mProp = a.match(/msg\\.getProperty\\s*\\(\\s*(?:"[^"]+"|'[^']+')\\s*,\\s*(?:"([^"]+)"|'([^']+)')\\s*\\)/);
                                          if (mProp) return mProp[1] || mProp[2];
                                          return a.replace(/["']/g, '').trim();
                                      });
                                      
                                      const props = JSON.parse(el.getAttribute('data-props') || '{}');
                                      props.headers = args;
                                      el.setAttribute('data-props', JSON.stringify(props));"""

found = False
for i in range(len(lines)):
    if target in lines[i]:
        indent = lines[i][:lines[i].find(target)]
        lines[i] = indent + replacement + "\n"
        found = True
        break

if found:
    with open(file_path, 'w') as f:
        f.writelines(lines)
    print("Successfully replaced.")
else:
    print("Target not found.")
