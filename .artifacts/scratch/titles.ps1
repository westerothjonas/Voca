Get-ChildItem -Path 'C:\Users\CWest\AndroidStudioProjects\Voca\.artifacts\extracted_stitches' -Recurse -Filter 'code.html' | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    if ($content -match '<title>(.*?)</title>') {
        [PSCustomObject]@{ Path = $_.Directory.Name; Title = $Matches[1] }
    }
}
