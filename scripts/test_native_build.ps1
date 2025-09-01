# BoofCV原生镜像构建测试脚本
# PowerShell版本

Write-Host "🚀 开始BoofCV原生镜像构建测试" -ForegroundColor Green

# 检查是否在正确的目录
if (-not (Test-Path "pom.xml")) {
    Write-Host "❌ 请在项目根目录运行此脚本" -ForegroundColor Red
    exit 1
}

# 函数：运行命令并显示结果
function Run-Command {
    param(
        [string]$Command,
        [string]$Description
    )
    
    Write-Host "`n🔄 $Description" -ForegroundColor Blue
    Write-Host "💻 执行命令: $Command" -ForegroundColor Gray
    
    $startTime = Get-Date
    
    try {
        $result = Invoke-Expression $Command 2>&1
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        
        Write-Host "⏱️  耗时: $([math]::Round($duration, 2))秒" -ForegroundColor Gray
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ $Description 成功" -ForegroundColor Green
            if ($result) {
                Write-Host "📝 输出:" -ForegroundColor Gray
                Write-Host $result
            }
            return $true
        } else {
            Write-Host "❌ $Description 失败" -ForegroundColor Red
            Write-Host "📝 错误输出:" -ForegroundColor Red
            Write-Host $result
            return $false
        }
    }
    catch {
        Write-Host "❌ 执行命令时出错: $_" -ForegroundColor Red
        return $false
    }
}

# 测试步骤
$steps = @(
    @(".\mvnw.cmd clean", "清理项目"),
    @(".\mvnw.cmd compile", "编译项目"),
    @(".\mvnw.cmd test", "运行测试"),
    @(".\mvnw.cmd package -Dnative -DskipTests=true", "构建原生镜像")
)

foreach ($step in $steps) {
    $cmd = $step[0]
    $description = $step[1]
    
    if (-not (Run-Command -Command $cmd -Description $description)) {
        Write-Host "`n💥 测试在 '$description' 步骤失败" -ForegroundColor Red
        exit 1
    }
}

Write-Host "`n🎉 所有测试步骤都成功完成!" -ForegroundColor Green

# 检查生成的二进制文件
$binaryPath = "target\qrcode-server-1.0-SNAPSHOT-runner.exe"
if (Test-Path $binaryPath) {
    Write-Host "✅ 原生二进制文件已生成: $binaryPath" -ForegroundColor Green
    
    # 尝试运行二进制文件进行快速测试
    Write-Host "`n🧪 进行快速启动测试..." -ForegroundColor Blue
    
    $job = Start-Job -ScriptBlock { 
        param($path)
        & $path
    } -ArgumentList (Resolve-Path $binaryPath)
    
    Start-Sleep -Seconds 5
    Stop-Job $job
    Remove-Job $job
    
    Write-Host "✅ 快速启动测试完成" -ForegroundColor Green
} else {
    Write-Host "❌ 未找到原生二进制文件" -ForegroundColor Red
}

Write-Host "`n🏁 测试完成!" -ForegroundColor Green
