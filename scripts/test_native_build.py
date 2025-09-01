#!/usr/bin/env python3
"""
本地测试BoofCV原生镜像构建
"""
import subprocess
import sys
import time
from pathlib import Path

def run_command(cmd, description):
    """运行命令并显示结果"""
    print(f"\n🔄 {description}")
    print(f"💻 执行命令: {cmd}")
    
    start_time = time.time()
    try:
        result = subprocess.run(cmd, shell=True, capture_output=True, text=True, encoding='utf-8')
        end_time = time.time()
        
        print(f"⏱️  耗时: {end_time - start_time:.2f}秒")
        
        if result.returncode == 0:
            print(f"✅ {description} 成功")
            if result.stdout.strip():
                print("📝 输出:")
                print(result.stdout)
        else:
            print(f"❌ {description} 失败")
            print("📝 错误输出:")
            print(result.stderr)
            return False
            
    except Exception as e:
        print(f"❌ 执行命令时出错: {e}")
        return False
    
    return True

def main():
    """主函数"""
    print("🚀 开始BoofCV原生镜像构建测试")
    
    # 检查是否在正确的目录
    if not Path("pom.xml").exists():
        print("❌ 请在项目根目录运行此脚本")
        sys.exit(1)
    
    # 测试步骤
    steps = [
        ("./mvnw clean", "清理项目"),
        ("./mvnw compile", "编译项目"),
        ("./mvnw test", "运行测试"),
        ("./mvnw package -Dnative -DskipTests=true", "构建原生镜像"),
    ]
    
    for cmd, description in steps:
        if not run_command(cmd, description):
            print(f"\n💥 测试在 '{description}' 步骤失败")
            sys.exit(1)
    
    print("\n🎉 所有测试步骤都成功完成!")
    
    # 检查生成的二进制文件
    binary_path = Path("target") / "qrcode-server-1.0-SNAPSHOT-runner"
    if binary_path.exists():
        print(f"✅ 原生二进制文件已生成: {binary_path}")
        
        # 尝试运行二进制文件进行快速测试
        print("\n🧪 进行快速启动测试...")
        test_cmd = f"timeout 10 {binary_path}"
        run_command(test_cmd, "快速启动测试")
    else:
        print("❌ 未找到原生二进制文件")

if __name__ == "__main__":
    main()
