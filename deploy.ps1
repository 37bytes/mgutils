Write-Host "Starting deploy script"

Push-Location

# Automatically obtain groupId, artifactId and version
#$groupId = mvn help:evaluate -Dexpression=project.groupId -q -DforceStdout
#$artifactId = mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout
$groupId = "dev.b37.libs"
$artifactId = "mgutils"
$version = mvn help:evaluate "-Dexpression=project.version" -q -DforceStdout

Write-Host "Publishing $groupId $artifactId $version"

# First clean install in base folder to ensure the artifact is present
mvn clean install

# Create new repository directory
$baseDir = Get-Location
$targetDir = Join-Path $baseDir 'target'
$baseDirName = Split-Path $baseDir -Leaf

$repoDir = Join-Path $baseDir "${baseDirName}-repository"

# Copy git folder if the directory didn't exist yet
if (!(Test-Path $repoDir)) {
    New-Item -ItemType Directory -Path $repoDir | Out-Null
    Write-Host "Copying git folder from original project..."
    Copy-Item (Join-Path $baseDir '.git') (Join-Path $repoDir '.git') -Recurse
}

# Check out to new branch called repository
Write-Host "Setting up repository branch and installing jar..."
$jarLocation = Join-Path $targetDir "${artifactId}-${version}.jar"
Set-Location $repoDir
$existedInRemote = git ls-remote --heads origin repository

if ([string]::IsNullOrEmpty($existedInRemote)) {
    git checkout -B repository
} else {
    git fetch origin repository
    git checkout repository
}

mvn -f "../pom.xml" "install:install-file" -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dfile=$jarLocation -Dpackaging=jar -DgeneratePom=$true "-DlocalRepositoryPath=." -DcreateChecksum=$true

# Commit and push
Write-Host "Committing and pushing to repository branch..."
git add -A .
git commit -m "Release version ${version}"
git push origin repository --force

Write-Host "Done"

git checkout master
Pop-Location